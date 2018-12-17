//服务层
app.service('sellerService',function($http){

	//增加 
	this.add=function(entity){
		return  $http.post('../seller/add.do',entity );
	};
});