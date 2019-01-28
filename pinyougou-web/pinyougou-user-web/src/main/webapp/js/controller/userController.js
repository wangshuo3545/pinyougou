/** 定义控制器层 */
app.controller('userController', function($scope, $timeout, baseService){

    $scope.user = {};

    // 定义注册用户的方法
    $scope.save = function () {
        // 判断密码是否一致
        if ($scope.okPassword == null
            || $scope.okPassword != $scope.user.password){
            alert("两次密码不一致！");
        }else{
            // 发送异步请求
            baseService.sendPost("/user/save?code=" + $scope.code, $scope.user).then(function(response){
                // 获取响应数据
                if (response.data){
                    // 清空数据
                    $scope.user = {};
                    $scope.okPassword = "";
                    $scope.code = "";
                    alert("注册成功！");
                }else{
                    alert("注册失败！");
                }
            });
        }
    };

    // 定义按钮是否禁用的变量
    $scope.disabled = false;
    $scope.tip = "获取短信验证码";

    // 获取短信验证码
    $scope.sendSmsCode = function () {
        // 判断手机号码
        if (!$scope.user.phone || !/^1[3|4|8|5|6|2]\d{9}$/.test($scope.user.phone)){
            alert("手机号码格式不正确！");
        }else{
            $scope.disabled = true;
            // 发送异步请求
            baseService.sendGet("/user/sendSmsCode?phone=" + $scope.user.phone)
               .then(function(response){
                    // 获取响应数据
                   if (response.data){
                       // 调用倒计时方法
                       $scope.downcount(90);
                   }else {
                       alert("短信发送失败！");
                   }
            });
        }
    };

    // 定义倒计时的方法
    $scope.downcount = function (seconds) {
        seconds--;
        if (seconds >= 0){
            $scope.tip = seconds + "S, 后重新获取！";
            /**
             * 开启定时器
             * 第一个参数：回调的函数
             * 第二个参数：时间毫秒数
             */
            $timeout(function(){
                $scope.downcount(seconds);
            }, 1000);
        }else{
            $scope.disabled = false;
            $scope.tip = "获取短信验证码";
        }
    };

});