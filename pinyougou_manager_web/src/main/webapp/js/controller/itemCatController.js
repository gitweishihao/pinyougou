 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			//新增分类时,要指定分类的上级id
			$scope.entity.parentId = $scope.parentId;
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.findByParentId ($scope.parentId);//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		if(confirm("你确定要删除吗？")){
            //获取选中的复选框
            itemCatService.dele( $scope.ids ).success(
                function(response){
                    if(response.success){
                    	//删除成功
                        $scope.findByParentId($scope.parentId);//刷新列表
                    }else{
                    	alert(response.message);
					}
                }
            );
        }
	};
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};

	//记录当前分类的上级id
	$scope.parentId = 0;
	//根据上级id查询商品数据列表
	$scope.findByParentId = function (parentId) {

		//当查询下级时，给当前分类的上级id赋值
		$scope.parentId = parentId;
		itemCatService.findByParentId(parentId).success(function (response) {
			$scope.list = response;
        })
    };

    //导航栏实现
    //定义分类级别变量，默认是顶级分类
	$scope.grade = 0;
	//定义点击查询下级后，分类级别加一，参数grade是分类级别加一后的分类数据
	$scope.setGrade = function (grade) {
		//给分类级别赋值
		$scope.grade = grade;
    };

	//面包屑导航栏效果实现   entity_p:父分类对象
	$scope.selectItemCatList = function (entity_p) {

		//面包屑导航栏展示与分类级别有关
		//如果是查询一级分类
		if($scope.grade == 0){
			$scope.entity_1 = null;
			$scope.entity_2 = null;
		}
		//如果是查询二级分类
		if($scope.grade == 1){
			$scope.entity_1 = entity_p;
			$scope.entity_2 = null;
		}
		//如果是查询三级分类
		if($scope.grade == 2){
			$scope.entity_2 = entity_p;
		}
		//单击导航栏面包屑，查询上级分类数据
		$scope.findByParentId(entity_p.id);
    };

	//查询所有模板数据
	$scope.templateList = function () {
		typeTemplateService.findAll().success(function (response) {
			$scope.templatelist = response;
        })
    }


});
