//自定义规格控制器
app.controller("specificationController",function ($scope,$controller,specificationService) {
    //继承父类控制器
    $controller("baseController",{$scope:$scope});

    //分页查询所有
    $scope.findPage = function (pageNum,pageSize) {
        specificationService.findPage(pageNum,pageSize).success(function (response) {
            $scope.paginationConf.totalItems = response.total;
            $scope.list = response.rows;
        })
    };

    //定义封装查询条件的对象
    $scope.searchEntity = {};
    //定义条件查询方法
    $scope.search = function (pageNum,pageSize) {
        specificationService.search($scope.searchEntity,pageNum,pageSize).success(function (response) {

            //总记录数分给分页配置
            $scope.paginationConf.totalItems = response.total;
            //将响应结果对象的数据列表赋给list
            $scope.list = response.rows;
        })
    };

    //保存
    //定义请求json格式参数
    /*
        entity:{
                specification:{
                        specName:"屏幕尺寸"，
                        },
                specificationoptions:[
                        {optionName:"5寸",orders:1},
                        {optionName:"6寸",orders:2}
                ]
            }
    */
    $scope.save = function () {
        var method = null;
        //根据规格id是否有判断执行方法
        if($scope.entity.tbSpecification.id != null){
            //修改保存
            method = specificationService.update($scope.entity);
        }else{
            //添加保存
            method = specificationService.add($scope.entity);
        }

        //执行方法
        method.success(function (response) {
            if(response.success){
                //添加成功，重新加载（刷新）规格列表
                $scope.reloadList();
            }else{
                //添加失败，弹出框展示提示信息
                alert(response.message);
            }
        });
    };

    //根据规格id查询
    $scope.findOne = function (id) {
        specificationService.findOne(id).success(function (response) {
            $scope.entity = response;
        })
    };

    //根据规格ids删除
    $scope.dele = function () {
      //弹出框判读是否要删除
        if(confirm("您确定要删除吗？")){
            specificationService.dele($scope.ids).success(function (response) {
                //判断是否删除成功
                if(response.success){
                    //删除成功，重新加载规格列表
                    $scope.reloadList();
                }else{
                    //删除失败，展示提示信息
                    alert(response.message);
                }
            });
        }
    };

    //初始化entity对象
    $scope.entity = {
        specificationOptionList:[]
    };

    //新增规格选项行
    $scope.addRow = function () {
        $scope.entity.specificationOptionList.push({});
    };
    //删除规格选项行
    $scope.deleRow = function (index) {
        $scope.entity.specificationOptionList.splice(index,1);
    }
});