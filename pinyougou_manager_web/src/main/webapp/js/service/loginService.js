app.service("loginService",function ($http) {

    //基于安全框架获取用户名
    this.getName = function () {
        return $http.get("../login/getName.do");
    }
});