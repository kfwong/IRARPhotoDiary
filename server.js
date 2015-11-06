#!/bin/env node
//  OpenShift sample Node application
var express = require('express');
var fs      = require('fs');
var http    = require('http');
var url     = require('url');
var bodyParser = require('body-parser');


/**
 *  Define the sample application.
 */
var SampleApp = function() {

    //  Scope.
    var self = this;


    /*  ================================================================  */
    /*  Helper functions.                                                 */
    /*  ================================================================  */

    /**
     *  Set up server IP address and port # using env variables/defaults.
     */
    self.setupVariables = function() {
        //  Set the environment variables we need.
        self.ipaddress = process.env.OPENSHIFT_NODEJS_IP;
        self.port      = process.env.OPENSHIFT_NODEJS_PORT || 8080;

        if (typeof self.ipaddress === "undefined") {
            //  Log errors on OpenShift but continue w/ 127.0.0.1 - this
            //  allows us to run/test the app locally.
            console.warn('No OPENSHIFT_NODEJS_IP var, using 127.0.0.1');
            self.ipaddress = "127.0.0.1";
        }

        // default to a 'localhost' configuration:
        self.connection_string = '127.0.0.1:27017/app';
        // if OPENSHIFT env variables are present, use the available connection info:
        if(process.env.OPENSHIFT_MONGODB_DB_PASSWORD){
          self.connection_string = process.env.OPENSHIFT_MONGODB_DB_USERNAME + ":" +
          process.env.OPENSHIFT_MONGODB_DB_PASSWORD + "@" +
          process.env.OPENSHIFT_MONGODB_DB_HOST + ':' +
          process.env.OPENSHIFT_MONGODB_DB_PORT + '/' +
          process.env.OPENSHIFT_APP_NAME;
        }
    };


    /**
     *  Populate the cache.
     */
    self.populateCache = function() {
        if (typeof self.zcache === "undefined") {
            self.zcache = { 'index.html': '' };
        }

        //  Local cache for static content.
        self.zcache['index.html'] = fs.readFileSync('./index.html');
    };


    /**
     *  Retrieve entry (content) from cache.
     *  @param {string} key  Key identifying content to retrieve from cache.
     */
    self.cache_get = function(key) { return self.zcache[key]; };


    /**
     *  terminator === the termination handler
     *  Terminate server on receipt of the specified signal.
     *  @param {string} sig  Signal to terminate on.
     */
    self.terminator = function(sig){
        if (typeof sig === "string") {
           console.log('%s: Received %s - terminating sample app ...',
                       Date(Date.now()), sig);
           process.exit(1);
        }
        console.log('%s: Node server stopped.', Date(Date.now()) );
    };


    /**
     *  Setup termination handlers (for exit and a list of signals).
     */
    self.setupTerminationHandlers = function(){
        //  Process on exit and signals.
        process.on('exit', function() { self.terminator(); });

        // Removed 'SIGPIPE' from the list - bugz 852598.
        ['SIGHUP', 'SIGINT', 'SIGQUIT', 'SIGILL', 'SIGTRAP', 'SIGABRT',
         'SIGBUS', 'SIGFPE', 'SIGUSR1', 'SIGSEGV', 'SIGUSR2', 'SIGTERM'
        ].forEach(function(element, index, array) {
            process.on(element, function() { self.terminator(element); });
        });
    };


    /*  ================================================================  */
    /*  App server functions (main app logic here).                       */
    /*  ================================================================  */

    /**
     *  Create the routing table entries + handlers for the application.
     */
    self.createRoutes = function() {
        self.routes = { };

        /*
        self.routes['/asciimo'] = function(req, res) {
            var link = "http://i.imgur.com/kmbjB.png";
            res.send("<html><body><img src='" + link + "'></body></html>");
        };

        self.routes['/'] = function(req, res) {
            res.setHeader('Content-Type', 'text/html');
            res.send(self.cache_get('index.html') );
        };
        */

        self.app.post('/albums', function(req, res){
        var object = req.body;
            self.db.collection('albums', {}, function(err, collection) {
                collection.insert(object, function(err, data) {
                    res.send(data);
                });
            });
        });

        self.app.get('/albums', function(req, res){
            self.db.collection('albums').find().toArray(function(err, data){
                res.send(data);
            });
        });


        self.app.get('/albums/images', function(req, res){

            self.db.collection('albums').distinct("imageProfiles",{},function(err, data){
                res.send(data);
            });
        });

        self.app.post('/albums/images', function(req, res){

            var tagsRaw = req.body;
            var tagsArray = new Array();
            
            tagsRaw.tags.forEach(function(tagRaw){
                tagsArray.push(tagRaw.tag);
            });

            self.db.collection('albums').aggregate([
                { $unwind : "$imageProfiles" },
                { $project : { "imageProfiles" : 1, "_id" : 0}},
                { $group : { _id : "$imageProfiles"}},
                { $unwind : "$_id.tags"},
                { $match : { "_id.tags.tag" : { $in : tagsArray }}},
                { $group : {_id : {"title" : "$_id.title", "description" : "$_id.description", "extension": "$_id.extension", "filename" : "$_id.filename", "latitude" : "$_id.latitude", "longitude" : "$_id.longitude", "order": "$_id.order"}, tags : { $addToSet : "$_id.tags"}}}
            ], function(err, data){
                
                var formatted = new Array();

                data.forEach(function(entry){
                    entry._id.tags = entry.tags;
                    formatted.push(entry._id);
                });
                
                res.send(formatted);
                
            });

        });
            
        self.app.get('/images', function(req, res){
            self.db.collection('images').find().toArray(function(err,data){
                    res.send(data);
            });    
        });
        
        self.app.get('/images/search', function(req, res){
            var tolerance = 28;
            var results = [];
            var DEBUG_HTML = "";
            
            var url_parts = url.parse(req.url, true);
            var query = url_parts.query;
            var sampleLabColor = query.labColor;
            
            /////
            self.db.collection('images').find().each(function(err, data){
                if(data === null) res.send(DEBUG_HTML);

                var targetLabColor = data.labColors[0];
                var deltaE = self.computeDeltaE(sampleLabColor, targetLabColor);
                //console.log("deltaE:"+deltaE);
                if(deltaE < tolerance){
                    //console.log(data.description);
                    DEBUG_HTML += "<strong>"+deltaE+":"+data.description+"</strong><img src='http://res.cloudinary.com/dxspdhqz3/image/upload/w_0.2/"+data.filename+"."+data.extension+"'/><br/>";
                }
            });
        });
    };


    /**
     *  Initialize the server (express) and create the routes and register
     *  the handlers.
     */
    self.initializeServer = function() {
        /*
        self.createRoutes();
        self.app = express.createServer();

        //  Add handlers for the app (from the routes).
        for (var r in self.routes) {
            self.app.get(r, self.routes[r]);
        }*/

        self.app = express();
        self.app.use(bodyParser.urlencoded({ extended: true }));
        self.app.use(bodyParser.json());
        self.server = http.createServer(self.app);
        self.createRoutes();
    };

    /**
     *  Initilize the mongodb & pooling, reuse the variable.
     */

    self.initializeDatabase = function(){
        //load the Client interface
        var MongoClient = require('mongodb').MongoClient;
        // the client db connection scope is wrapped in a callback:
        MongoClient.connect('mongodb://'+self.connection_string, function(err, db) {
          if(err) throw err;

          self.db = db;

        });
    }


    /**
     *  Initializes the sample application.
     */
    self.initialize = function() {
        self.setupVariables();
        self.populateCache();
        self.setupTerminationHandlers();

        // Create the express server and routes.
        self.initializeServer();
        self.initializeDatabase();
    };


    /**
     *  Start the server (starts up the sample application).
     */
    self.start = function() {
        //  Start the app on the specific interface (and port).
        self.app.listen(self.port, self.ipaddress, function() {
            console.log('%s: Node server started on %s:%d ...',
                        Date(Date.now() ), self.ipaddress, self.port);
        });
    };

};   /*  Sample Application.  */



/**
 *  main():  Main code.
 */
var zapp = new SampleApp();
zapp.initialize();
zapp.start();

