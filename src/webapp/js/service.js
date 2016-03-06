(function () {
    angular.module("MonitorAppSer", [])
        .factory("API", function ($http, $timeout, CONST) {
            return {
                add: function (key, value) {
                    return $http.post(CONST.URL + '/api', {key: key, value: value})
                },
                rm: function (key) {
                    return $http.delete(CONST.URL + '/api?key=' + key)
                },
                up: function (key, value) {
                    return $http.put(CONST.URL + '/api', {key: key, value: value})
                },
                status: function (callback) {
                    var me = this
                    $http.get(CONST.URL + '/status').success(function (data) {
                        var check = {};
                        for (var i = 0; i < data.length; i++) {
                            check[data[i].id] = {};
                            for (var j = 0; j < data.length; j++) {
                                check[data[i].id][data[j].id] = true;
                                for (var k = 0; k < data[i].ch.length; k++) {
                                    if (data[i].ch.hash != data[j].ch.hash && data[i].ch.id != data[j].ch.id)
                                        check[data[i].id][data[j].id] = false;
                                }
                            }
                        }
                        callback(data, check);
                    }).error(function () {
                        console.log('error');
                        $timeout(function () {
                            me.status(callback)
                        }, 5000);
                    })
                }
            }
        })
        .factory('DATA', function (CONST) {
            return {
                getSVGch: function (newData, real) {
                    if (newData) {
                        var step = (2 * Math.PI) / (real ? CONST.MAX_HASH : newData.length)
                        var res = []
                        for (var i = 0; i < newData.length; i++) {
                            var hash = real ? newData[i].hash + CONST.MAX_HASH / 2 : i;
                            res.push({
                                bx: newData[i].ax || CONST.CX,
                                by: newData[i].ay || CONST.CY,
                                ax: Math.cos(hash * step - Math.PI / 2) * CONST.R + CONST.CX,
                                ay: Math.sin(hash * step - Math.PI / 2) * CONST.R + CONST.CY,
                                id: newData[i].id,
                                hash: newData[i].hash,
                                stroke: newData[i].stroke || "white"
                            });
                        }
                        //console.log(res);
                        return res;
                    }
                },
                getStore: function (data) {
                    var me = this;
                    var res = {}
                    angular.forEach(data, function (item) {
                        res[item.id] = {
                            color: me.heatMapColorforValue(item.store.length > CONST.MAX_STORE ?
                                1 : item.store.length / CONST.MAX_STORE),
                            list: item.store
                        }
                    });
                    return res;
                },
                heatMapColorforValue: function (value) {
                    var h = (1.0 - value) * 220
                    return "hsl(" + h + ", 100%, 50%)";
                },
                redGreenValue: function (value){
                    var red = Math.round(255 - (150 * value))
                    var green = Math.round(200 * value)
                    var blue = 0
                    return "rgb("+red+','+green+','+blue+')'
                },
                getCheckValue: function(check){
                    var r = 0;
                    var N = 0;
                    for(var i in check){
                        var row = check[i]
                        for (var j in row){
                            r += check[i][j] ? 1 : 0;
                            N++;
                        }
                    }
                    //for (var i=0; i<check.key.length; i++){
                    //    for (var j=0; j<check[0].key.length; j++){
                    //        r = check[i][j] ? 1 : 0;
                    //    }
                    //}
                    return r/N;
                }
            }
        })
        .value("CONST", {
            URL: 'http://127.0.0.1:8080',
            MAX_HASH: Math.pow(2, 32),
            CX: 500 / 2,
            CY: 500 / 2,
            R: 220,
            MAX_STORE: 10
        })
})();