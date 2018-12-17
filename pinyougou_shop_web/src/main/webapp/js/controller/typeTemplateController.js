 //控制层 依赖注入brandService和specificationService用于发送请求，调用响应数据
app.controller('typeTemplateController' ,function($scope,$controller,typeTemplateService,brandService,specificationService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//将响应回来的json字符串转换为json对象数据  转换方式有两种：JSON.parse()  eval();
				$scope.entity.brandIds = JSON.parse($scope.entity.brandIds);
				$scope.entity.specIds = JSON.parse($scope.entity.specIds);
				$scope.entity.customAttributeItems = JSON.parse($scope.entity.customAttributeItems);
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.ids ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};

	//查询模板关联品牌数据列表
	//定义模板关联的品牌json数据格式
	$scope.brandList = {data:[]};

    $scope.selectBrandList = function () {
		brandService.selectBrandList().success(function (response) {
			$scope.brandList.data = response;
        })
    };

    //查询模板关联规格数据列表
	//定义模板关联的规格json数据格式数组
	$scope.specificationList = {data:[]};

	$scope.selectSpecificationList = function () {
		specificationService.selectSpecificationList().success(function (response) {
			$scope.specificationList.data = response;
        })
    };

    //定义扩展属性数组
    $scope.entity = {customAttributeItems:[]};
    //新建扩展属性行
	$scope.addRow = function () {
		$scope.entity.customAttributeItems.push({});
    };

    //删除扩展属性行
	$scope.deleRow = function (index) {
		$scope.entity.customAttributeItems.splice(index,1);
    };

    //将列表数据转成text内容
	$scope.jsonToString = function (jsonString, key) {
		//将json字符串转换为json对象数据
		var json = JSON.parse(jsonString);
		//定义字符串拼接变量
		var value = "";
		//循环遍历
		for(var i = 0; i< json.length;i++){
			//判断循环变量是否是1
			if(i > 0){
				value += ",";
			}
			//js中获取对象的属性值有两种方式：确定属性名时：对象.属性名  不确定属性名时：对象.[变量]
			value += json[i][key];
		}
		return value;
    }
});	
