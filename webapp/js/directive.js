(function () {
    angular.module("MonitorAppDir", [])
        .directive("node", function () {
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
            };
        });
})();
