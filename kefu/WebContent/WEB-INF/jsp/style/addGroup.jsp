<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jstl/core_rt" %>    
<!doctype html>
<html lang="zh-cn">
<head>
<meta charset="utf-8">
<meta name="author" content="dobao">
<title>客服信息管理系统</title>
<link rel="icon" href="favicon.ico">
<link href="/css/bootstrap.min.css" rel="stylesheet" type="text/css">
<link href="/css/bootstrap.google.v2.3.2.css" rel="stylesheet" type="text/css">
<link href="/css/app.css" rel="stylesheet" type="text/css">
</head>

<body>

<!-- 表格有边框 -->
<form id="mainForm">
<table class="table table-bordered m-table">
    <tbody>
        <tr>
            <td class="f-txtr tdbg">
            	<input type="hidden" id="id" name="id" value="${group.id}"/>
            	<input type="hidden" id="styleId" name="styleId" value="${group.styleId}"/>
            	<input type="text" id="name" name="name" value="${group.name}"/>
            </td>
            <td class="f-txtr tdbg">
            	<button type="submit" class="btn btn-primary" id="btn_save">保存<i class="icon-ok icon-white"></i></button>
			</td>
        </tr>
    </tbody>
</table>
                    
</form>


<script type="text/javascript" src="/js/jquery.min.js"></script>
<script type="text/javascript" src="/js/bootstrap.js"></script>
<script type="text/javascript" src="/jsplugin/lhgdialog/lhgdialog.min.js?skin=iblue"></script>

<script type="text/javascript">
var api = frameElement.api;//调用父页面数据  
var W = api.opener;//获取父页面对象  

//先检验,后保存
$('#btn_save').on('click',function(){
	var name = $("#name").val();
	if(name==''){
		$.dialog.alert("请填写名称!");
		return false;
	}
	var data = {
			"id":$("#id").val(),
			"name":$("#name").val(),
			"styleId":$("#styleId").val()
	};
	$.ajax({
	    type: "get",
	    url: "/busiGroup/validate.action",
	    dataType: "json",
	    data : data,
	    async:false,
	    success: function (data) {
	    	if(data.result==0){
	    		save();
	    	}else{
	    		$.dialog.alert(data.msg);
	    	}
	    },
	    error: function (msg) {
	    	$.dialog.alert(msg);
	    }
	});
    return false;
});

//保存
function save(){
	$.ajax({
		type : 'post',
		url :  "/busiGroup/save.action",
		dataType : 'json',
		data : $('#mainForm').serialize(),
		async:false,
	 	success: function (data) {
	    	if(data.result==0){
	    		W.$.dialog.alert('操作成功!',function(){
	    			W.addCallback();
	    		});
	    	}else{
	    		$.dialog.alert(data.msg);
	    	}
	    },
	    error: function (msg) {
	    	$.dialog.alert(msg);
	    }
	});
}

</script>

</body>
</html>