(function() {
    angular.module("MonitorAppCtrl", [])
        .controller("StatusCtr", function($scope, $timeout, API, DATA) {

            $scope.refresh = function() {
                $scope.loading = true;
                API.status(function(data, check) {
                    $scope.chCheck = DATA.getCheckValue(check);
                    $scope.chCheckColor = DATA.redGreenValue($scope.chCheck);
                    $scope.ch = DATA.getSVGch(data[0].ch, true);

                    $scope.store = DATA.getStore(data);
                    if ($scope.select && $scope.store[$scope.select.id])
                        $scope.select.store = $scope.store[$scope.select.id].list;
                    $scope.loading = false;
                });
            };
            $scope.refresh();

            $scope.addKey = function(key, value) {
                waitAndRefresh(API.add(key, value));
                $scope.value = "";
                $scope.key = "";
            };

            $scope.removeData = function(key) {
                waitAndRefresh(API.rm(key));
            };

            $scope.updateData = function(key, value) {
                waitAndRefresh(API.up(key, value));
            };

            $scope.overNodeIn = function(n) {
                angular.forEach($scope.ch, function(item) {
                    item.stroke = "white";
                });
                n.stroke = "red";
                $scope.select = {
                    id: n.id,
                    store: $scope.store[n.id].list,
                    hash: n.hash
                };
            };

            $scope.overNodeOut = function(n) {
                //n.stroke = "black";
            };

            $scope.clickNode = function(n) {
                //$scope.select = {id: n.id, store:$scope.store[n.id], hash: n.hash};
            };

            $scope.overSvgIn = function(n) {
                //console.log("in")
                $scope.ch = DATA.getSVGch($scope.ch, false);
                //$scope.ch = getSvgCh($scope.data, false);
            };

            $scope.overSvgOut = function(n) {
                //console.log("out")
                $scope.ch = DATA.getSVGch($scope.ch, true);
                //$scope.select = null;
                //$scope.ch = getSvgCh($scope.data, true, 1000);
            };

            function waitAndRefresh(promis) {
                promis.success(function(data) {
                    console.log(data);
                    if (data.status == 'OK') {
                        $scope.error = "";

                        $scope.loading = true;
                        $timeout(function() {
                            $scope.refresh();
                            //$scope.select.store = $scope.store[$scope.select.id].list
                        }, 1000);
                    } else {
                        $scope.error = data.data;
                        $timeout(function() {
                            $scope.error = "";
                        }, 2000);
                    }
                });
            }
        });
})();
