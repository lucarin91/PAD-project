(function () {

    angular.module("MonitorApp", [
            "MonitorAppCtrl",
            "MonitorAppSer",
            "MonitorAppDir"
        ])

        .value("CONST", {
            URL: 'http://127.0.0.1:8080',
            MAX_HASH: Math.pow(2, 64),
            CX: 500 / 2,
            CY: 500 / 2,
            R: 220,
            MAX_STORE: 10
        })

        .run(function (CONST) {
            var gui = require('nw.gui');
            var argv = gui.App.argv;
            console.log(gui.App.argv);
            if (argv.length > 0)
                CONST.URL = 'http://' + argv[0];
            if (process.env.npm_package_config_url)
                CONST.URL = 'http://' + process.env.npm_package_config_url;
        })

})();