<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="/WEB-INF/fmt.tld"%> 
<%@ page import="com.xiaoma.kefu.util.CheckCodeUtil"  %>  
<%@ page import="javax.servlet.http.HttpSession"  %>  
<%@ page import="com.xiaoma.kefu.model.User"  %>      
<!doctype html>
<html lang="zh-cn">
<head>
<meta charset="utf-8">
<title>客服信息管理系统</title>
<link rel="icon" href="favicon.ico">
<link href="/css/bootstrap.min.css" rel="stylesheet" type="text/css">
<link href="/css/bootstrap.google.v2.3.2.css" rel="stylesheet" type="text/css">
<link href="/css/app.css" rel="stylesheet" type="text/css">
<link href="/css/jquery.mCustomScrollbar.css" rel="stylesheet" type="text/css">
</head>

<body>
<!-- 面包屑 -->
<div class="m-crumb">
    <ul class="f-cb">
        <li><b>位置：</b></li>
        <li>记录中心</li>
        <li><i>&gt;</i>留言记录</li>
    </ul>
</div>
<!-- 查询条件 -->
<div class="g-cnt">
<div class="m-query f-mar10">
	<div class="m-query-hd">
        <label>对话时间：</label><input class="c-wd80 Wdate" type="text" id="beginDate" value="${beginDate }" onClick="WdatePicker()" /> - 
        <input class="c-wd80 Wdate" type="text" id="endDate" value="${endDate }" onClick="WdatePicker()" />
        <div class="u-subsec">
           	<button class="btn btn-primary" type="button" onclick="javascript:find(1);"> 查 询  </button>
        </div>
    </div>
    <div class="u-subsec">
     <%HttpSession session1 = request.getSession(); User user = (User)session1.getAttribute("user"); Integer userId = user.getId();%>
       <% if(CheckCodeUtil.isCheckFunc(userId,"f_checklog_del")) {%>
       <button type="button" class="btn btn-primary btn-small" onclick="del();">删除</button>
       <%} %>
       <% if(CheckCodeUtil.isCheckFunc(userId,"f_dialog_recycel")) {%>
       <button type="button" class="btn btn-primary btn-small" onclick="toRecycle();">回收站</button>
       <%} %>
   	</div>
    <div class="u-hr"></div>
</div>

<div id="table_data">
	<jsp:include page="messageList.jsp"></jsp:include>
</div>
</div>
<script type="text/javascript" src="/js/jquery.min.js"></script>
<script type="text/javascript" src="/js/bootstrap.js"></script>
<script type="text/javascript" src="/jsplugin/datepicker/WdatePicker.js"></script>
<script type="text/javascript" src="/jsplugin/lhgdialog/lhgdialog.min.js?skin=iblue"></script>
<script type="text/javascript" src="/js/jquery.mCustomScrollbar.concat.min.js"></script>
<script type="text/javascript" src="/js/app.js"></script>
<script type="text/javascript">
//自定义滚动条--左右布局右侧
(function($){
	$(window).load(function(){
		$(".g-cnt").mCustomScrollbar({theme:"minimal-dark"});
	});
})(jQuery);

function find(currentPage){
	var url="/recordsCenter/findMessage.action";
	var data = {
			"currentPage":currentPage,
			"pageRecorders" : $("#pageRecorders").val(),
			"typeId":1,
			"beginDate":$("#beginDate").val(),
			"endDate":$("#endDate").val()
	};
	$.ajax({
	    type: "get",
	    url: url,
	    data: data,
	    contentType: "application/json; charset=utf-8",
	    dataType: "html",
	    success: function (data) {
	       $("#table_data").html(data);
	    },
	    error: function (msg) {
	        alert(msg);
	    }
	});
}

//全选/全不选
function checkedAll(){
	if(($("#titleCheckbox").is(":checked"))){
		$("#table_data :checkbox").prop("checked", true);  
	}else{
		$("#table_data :checkbox").prop("checked", false);
	}
}

//回收站
function toRecycle(){
	var url="/recordsCenter/findMessageDel.action";
	window.location.href = url;
	return;
}

//删除
function del(){
	
	var ids = "";
	$("input[type=checkbox][name=detailCheckbox]:checked").each(function(){ 
    	if(ids!=""){
    		ids+=",";
    	}
   		ids+=$(this).val();
    });
	if(ids==""){
		$.dialog.alert("请先选择数据!");
		return;
	}
	
	$.dialog.confirm('你确定要删除吗？', function(){
		var url="/recordsCenter/deleteMsg4Logic.action";
		$.ajax({
		    type: "GET",
		    url: url,
		    data: {"ids":ids},
		    contentType: "application/json; charset=utf-8",
		    dataType: "json",
		    success: function (data) {
		    	if(data.result==0){
		    		$.dialog.alert(data.msg);
		    		find(1);
		    	}else{
		    		$.dialog.alert(data.msg);
		    	}
		    },
		    error: function (msg) {
		    	$.dialog.alert(msg);
		    }
		});
	});
	
}

//创建客户名称
function updateCusl(customerId,msgId){
	$.dialog({content:'url:/customer/editCus4Msg.action?customerId='+customerId+'&msgId='+msgId,
		id: 'editCus',
		width: 400,height: 500,
		title:'添加访客信息'
	});
}

//创建客户回调
function editCallback(){
	$.dialog({id:'editCus'}).close();
	var pageNo = '${pageBean.currentPage}';
	find(pageNo);
}

//查看明细
function showDetail(msgId){
// 	var url = '/messageRecords/view.action?msgId='+msgId;
// 	window.location = url;
// 	return;
	$.dialog({content:'url:/messageRecords/view.action?msgId='+msgId,
		id: 'viewMsg',
		width: 400,height: 500,
		title:'留言详情',
		cancel: true
	});
}
</script>
</body>
</html>
