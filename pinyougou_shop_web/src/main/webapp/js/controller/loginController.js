app.controller("loginController",function ($scope, $controller, loginService) {

    //继承父类控制器
    $controller("baseController",{$scope:$scope});

    //基于安全框架获取商家用户名
    $scope.getName = function () {
        loginService.getName().success(function (response) {
            $scope.loginName = response.loginName;
        })
    }
});