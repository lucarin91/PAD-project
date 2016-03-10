(function () {
    angular.module("MonitorAppCtrl", [])
        .controller("StatusCtr", function ($scope, $timeout, API, DATA) {

            //var colors = ['#ffffff', '#c1f0c1', '#6fdc6f', '#2db92d', '#196719']
            //var MAX = 10
            //var MAX_HASH = Math.pow(2, 32);
            $scope.refresh = function () {
                $scope.loading = true;
                API.status(function (data, check) {
                    $scope.chCheck = DATA.getCheckValue(check);
                    $scope.chCheckColor = DATA.redGreenValue( $scope.chCheck);
                    $scope.ch = DATA.getSVGch(data[0].ch, true);

                    //$scope.store = {}
                    //angular.forEach(data, function (item) {
                    //    $scope.store[item.id] = {
                    //        color: heatMapColorforValue(item.store.length > MAX ? 1 : item.store.length / MAX),
                    //        // colors[item.store.length > colors.length - 1 ? colors.length - 1 : item.store.length],
                    //        list: item.store
                    //    }
                    //    //console.log(item.id);
                    //});

                    $scope.store = DATA.getStore(data);
                    if ($scope.select) $scope.select.store = $scope.store[$scope.select.id].list
                    $scope.loading = false;
                })
            }
            $scope.refresh();


            //$http.get('http://127.0.0.1:8080/status/store').success(function (data) {
            //    console.log(data);
            //    $scope.store = {}
            //    angular.forEach(data,function(item){
            //        $scope.store[item.id] = {color: colors[item.store.length>colors.length-1 ? colors.length-1 : item.store.length], list: item.store}
            //        console.log(item.id);
            //    });
            //    console.log($scope.store)
            //})

            $scope.addKey = function (key, value) {
                waitAndRefresh(API.add(key, value))
                $scope.value = "";
                $scope.key = ""
            }

            $scope.removeData = function (key) {
                waitAndRefresh(API.rm(key))
            }

            $scope.updateData = function (key, value) {
                waitAndRefresh(API.up(key, value))
            }

            $scope.overNodeIn = function (n) {
                angular.forEach($scope.ch, function (item) {
                    item.stroke = "white"
                });
                n.stroke = "red";
                $scope.select = {id: n.id, store: $scope.store[n.id].list, hash: n.hash};
            }

            $scope.overNodeOut = function (n) {
                //n.stroke = "black";
            }

            $scope.clickNode = function (n) {
                //$scope.select = {id: n.id, store:$scope.store[n.id], hash: n.hash};
            }

            $scope.overSvgIn = function (n) {
                //console.log("in")
                $scope.ch = DATA.getSVGch($scope.ch, false)
                //$scope.ch = getSvgCh($scope.data, false);
            }

            $scope.overSvgOut = function (n) {
                //console.log("out")
                $scope.ch = DATA.getSVGch($scope.ch, true)
                //$scope.select = null;
                //$scope.ch = getSvgCh($scope.data, true, 1000);
            }

            function waitAndRefresh(promis) {
                promis.success(function (data) {
                    console.log(data);
                    $scope.loading = true;
                    $timeout(function () {
                        $scope.refresh();
                        //$scope.select.store = $scope.store[$scope.select.id].list
                    }, 1000)
                })
            }

            //function heatMapColorforValue(value) {
            //    var h = (1.0 - value) * 240
            //    return "hsl(" + h + ", 100%, 50%)";
            //}

            //var CX = 500 / 2;
            //var CY = 500 / 2;
            //var R = 220;

            //function getSvgCh(data, real) {
            //    var ch = data.ch;
            //    var step = (2 * Math.PI) / (real ? MAX_HASH : ch.length)
            //    var res = []
            //    var i = 0;
            //    angular.forEach(ch, function (item) {
            //        var hash = real ? item.hash + MAX_HASH / 2 : i++;
            //        res.push({
            //            bx: CX, by: CY,
            //            ax: Math.cos(hash * step - Math.PI / 2) * R + CX,
            //            ay: Math.sin(hash * step - Math.PI / 2) * R + CY,
            //            id: item.id,
            //            hash: item.hash,
            //            stroke: "white"
            //        });
            //    });
            //    //console.log(res);
            //    return res;
            //}

            //function updateSvgCh(real) {
            //    if ($scope.ch) {
            //        var step = (2 * Math.PI) / (real ? MAX_HASH : $scope.ch.length)
            //        var i = 0;
            //        angular.forEach($scope.ch, function (item) {
            //            var hash = real ? item.hash + MAX_HASH / 2 : i++;
            //            item.bx = item.ax;
            //            item.by = item.ay;
            //            item.ax = Math.cos(hash * step - Math.PI / 2) * R + CX;
            //            item.ay = Math.sin(hash * step - Math.PI / 2) * R + CY;
            //        });
            //        //console.log($scope.ch)
            //    }
            //}

            //    function getStatus(callback) {
            //        $http.get('http://127.0.0.1:8080/status').success(function (data) {
            //            //console.log(data);
            //            var check = {};
            //            for (var i = 0; i < data.length; i++) {
            //                check[data[i].id] = {};
            //                for (var j = 0; j < data.length; j++) {
            //                    check[data[i].id][data[j].id] = true;
            //                    for (var k = 0; k < data[i].ch.length; k++) {
            //                        if (data[i].ch.hash != data[j].ch.hash && data[i].ch.id != data[j].ch.id)
            //                            check[data[i].id][data[j].id] = false;
            //                    }
            //                }
            //            }
            //            //  console.log(check);
            //            callback(data, check);
            //        }).error(function (data) {
            //            console.log('error');
            //            $timeout(function () {
            //                $scope.refresh();
            //            }, 5000);
            //        })
            //    }
        })
})();