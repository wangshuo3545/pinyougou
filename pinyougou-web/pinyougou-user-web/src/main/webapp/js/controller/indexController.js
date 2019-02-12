/** 定义控制器层 */
app.controller('indexController', function($scope, baseService){
    // 定义获取登录用户名
    $scope.loadUsername= function () {
        // 发送异步请求
        baseService.sendGet("/user/showName").then(function(response){
            $scope.loginName = response.data.loginName;
        });
    };
});