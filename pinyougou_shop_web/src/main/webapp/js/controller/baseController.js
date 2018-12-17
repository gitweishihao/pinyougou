app.controller("baseController",function ($scope) {
    //angularjs分页配置
    $scope.paginationConf= {
        currentPage: 1,  				//当前页
        totalItems: 10,					//总记录数
        itemsPerPage: 10,				//每页记录数
        perPageOptions: [10, 20, 30, 40, 50], //分页选项，下拉选择一页多少条记录
        onChange: function () {			//页面变更后触发的方法
            $scope.reloadList();		//启动就会调用分页组件
        }
    };

//定义启动时调用分页查询的方法,即重新加载数据列表，即刷新
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    };

//跟新复选框的选中状态	$event:是angularjs提供的事件对象，用于判断复选框是否选中
//定义id数组
    $scope.ids = [];
    $scope.updateSelection = function ($event,id) {
        //判断选中状态
        if($event.target.checked){//选中状态
            $scope.ids.push(id);
        }else{
            //取消选中，移除当前id //参数一：移除位置元素的索引值 参数二：从该位置移除几个元素
            //获取当前数组元素的索引值
            var index= $scope.ids.indexOf(id);
            $scope.ids.splice(index,1);
        }
    };

    //分析勾选的规格列表：
    //[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["6寸","5寸"]}]
    //根据属性名获取属性值，在于传递过来的属性值做对比，用于判断规格名称是否存在于勾选的规格列表中
    //list:表示勾选的规格列表，key:规格名称属性名：attributeName，keyValue:当前勾选的规格选项对应的规格名称

    $scope.searchObjectByKey = function (list, key, keyValue) {
        //遍历规格列表
        for(var i = 0;i < list.length;i++){
            //判断规格名称是否存在于勾选的规格列表中  注意：在获取属性值时，如果不确定属性名:对象[变量],如果确定属性名：对象.属性名
            if(list[i][key] == keyValue){
                //如果存在，返回勾选的规格对象
                return list[i];
            }
        }
        //否则，返回null
        return null;
    }

});