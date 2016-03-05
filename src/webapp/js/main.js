(function () {
    var app = angular.module("MonitorApp", []);

    app.controller("StatusCtr", function ($scope, $http) {
        getStatusCh(function (data) {
            $scope.data = data[0];
            $scope.ch = getSvgCh(data[0], true, 1000);
        })

        $http.get('http://127.0.0.1:8080/status/store').success(function (data) {
            console.log(data);
            $scope.store = {}
            angular.forEach(data,function(item){
                $scope.store[item.id] = item.store;
            });
        })

        $scope.clickNode = function (n) {
            $scope.select = {id: n.id, store:$scope.store[n.id], hash: n.hash};
        }

        $scope.overNodeIn = function (n) {
            //console.log("in")
            updateSvgCh(false)
            //$scope.ch = getSvgCh($scope.data, false);
        }

        $scope.overNodeOut = function (n) {
            //console.log("out")
            updateSvgCh(true, 1000)
            $scope.select = null;
            //$scope.ch = getSvgCh($scope.data, true, 1000);
        }

        var CX = 800 / 2;
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
                    bx: 0, by: 0,
                    ax: Math.cos(hash * step - Math.PI / 2) * R + CX,
                    ay: Math.sin(hash * step - Math.PI / 2) * R + CY,
                    id: item.id,
                    hash: item.hash
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

        function getStatusCh(callback) {
            $http.get('http://127.0.0.1:8080/status/ch').success(function (data) {
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
                id: '='
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
})();