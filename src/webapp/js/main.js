(function () {
    var app = angular.module("MonitorApp", []);

    app.controller("StatusCtr", function ($scope, $http, $timeout) {

        var colors = ['#ffffff', '#c1f0c1', '#6fdc6f', '#2db92d', '#196719']
        $scope.refresh = function () {
            $scope.loading = true;
            getStatus(function (data, check) {
                $scope.data = data;
                $scope.ch = getSvgCh(data[0], true, 1000);

                $scope.store = {}
                angular.forEach(data, function (item) {
                    $scope.store[item.id] = {
                        color: colors[item.store.length > colors.length - 1 ? colors.length - 1 : item.store.length],
                        list: item.store
                    }
                    console.log(item.id);
                });
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
            waitAndRefresh($http.post('http://127.0.0.1:8080/api',{key: key, value: value}))
        }

        $scope.removeData = function(key){
            waitAndRefresh($http.delete('http://127.0.0.1:8080/api?key='+key))
        }

        $scope.updateData = function (key, value) {
            waitAndRefresh($http.put('http://127.0.0.1:8080/api',{key: key, value: value}))
        }

        function waitAndRefresh(promis){
            promis.success(function (data) {
                console.log(data);
                $scope.loading = true;
                $timeout(function () {
                    $scope.refresh();
                    //$scope.select.store = $scope.store[$scope.select.id].list
                }, 1000)
            })
        }

        $scope.overNodeIn = function (n) {
            angular.forEach($scope.ch, function (item) {
                item.stroke = "black"
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
            updateSvgCh(false)
            //$scope.ch = getSvgCh($scope.data, false);
        }

        $scope.overSvgOut = function (n) {
            //console.log("out")
            updateSvgCh(true, 1000)
            //$scope.select = null;
            //$scope.ch = getSvgCh($scope.data, true, 1000);
        }

        var CX = 500 / 2;
        var CY = 500 / 2;
        var R = 220;

        function getSvgCh(data, real, maxhash) {
            var ch = data.ch;
            var step = (2 * Math.PI) / (real ? (maxhash * 2) : ch.length)
            var res = []
            var i = 0;
            angular.forEach(ch, function (item) {
                var hash = real ? item.hash + maxhash : i++;
                res.push({
                    bx: CX, by: CY,
                    ax: Math.cos(hash * step - Math.PI / 2) * R + CX,
                    ay: Math.sin(hash * step - Math.PI / 2) * R + CY,
                    id: item.id,
                    hash: item.hash,
                    stroke: "black"
                });
            });
            console.log(res);
            return res;
        }

        function updateSvgCh(real, maxhash) {
            var step = (2 * Math.PI) / (real ? (maxhash * 2) : $scope.ch.length)
            var i = 0;
            angular.forEach($scope.ch, function (item) {
                var hash = real ? item.hash + maxhash : i++;
                item.bx = item.ax;
                item.by = item.ay;
                item.ax = Math.cos(hash * step - Math.PI / 2) * R + CX;
                item.ay = Math.sin(hash * step - Math.PI / 2) * R + CY;
            });
            //console.log($scope.ch)
        }

        function getStatus(callback) {
            $http.get('http://127.0.0.1:8080/status').success(function (data) {
                console.log(data);
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
                //  console.log(check);
                callback(data, check);
            })
        }
    })

    app.directive("node", function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                ax: '=',
                ay: '=',
                bx: '=',
                by: '=',
                id: '=',
                stroke: '=',
                fill: '='
            },
            templateUrl: 'templates/node.html',
            templateNamespace: 'svg',
            link: function (scope, element, attr) {
                //    console.log(element);
                scope.$watch('ax', function (newVal, oldVal) {
                    element[0].childNodes[5].beginElement();
                }, true);
            }
        }

    })


    //35% 	#239023	rgb(35, 144, 35)	hsl(120, 61%, 35%)
    //30% 	#1e7b1e	rgb(30, 123, 30)	hsl(120, 61%, 30%)
    //25% 	#196719	rgb(25, 103, 25)	hsl(120, 61%, 25%)
    //20% 	#145214
    //
})();