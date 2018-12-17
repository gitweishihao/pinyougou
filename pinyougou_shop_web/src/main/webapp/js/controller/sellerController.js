 //控制层 
app.controller('sellerController' ,function($scope,$controller   ,sellerService){	
	
	$controller('baseController',{$scope:$scope});//继承

	//商家入驻
	$scope.regist = function () {
		sellerService.add($scope.entity).success(function (response) {
			if(response.success){
				//注入成功，跳转到登陆页面
				location.href = "shoplogin.html";
			}else{
				//注入失败，展示错误提示信息
				alert(response.message);
			}
        })
    };
    
});	
