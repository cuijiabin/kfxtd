<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="/WEB-INF/fmt.tld"%>   
 
<!doctype html>
<html lang="zh-cn">
<head>
<meta charset="utf-8">
<title>客服信息管理系统</title>
<link rel="icon" href="favicon.ico">
<link href="/css/bootstrap.min.css" rel="stylesheet" type="text/css">
<link href="/css/bootstrap.google.v2.3.2.css" rel="stylesheet" type="text/css">
<link href="/css/app.css" rel="stylesheet" type="text/css">
</head>

<body>
<!-- 面包屑 -->
<div class="m-crumb">
    <ul class="f-cb">
        <li><b>位置：</b></li>
        <li><a href="#">首页</a></li>
        <li><i>&gt;</i><a href="#">访客管理</a></li>
        <li><i>&gt;</i>黑名单</li>
    </ul>
</div>
<!-- 查询条件 -->
<div class="m-query f-mar10">
	
    <div class="u-hr"></div>
    <div class="m-query-bd">
        <div class="f-mbm">
            <label>客户编号：</label><input id="customerId" name="customerId" value="${customerId }" class="c-wd150" type="text" />
            <label>添加工号：</label><input  id="userId" name="userId" class="c-wd150" type="text" />
            <label>阻止原因：</label><input id="description" name="description" value="${description}" class="c-wd150" type="text" />
               <button type="button" class="btn btn-primary btn-small" onclick="javascript:find(1);">查询</button>
        </div>
        <div class="f-mbm">
         
        <button type="button" class="btn btn-primary btn-small" onclick="javascript:addBlacklist();">添加黑名单</button>
            <label></label>
            <button type="button" class="btn btn-primary btn-small" onclick="deleteList();">删除</button>
        </div>
        <div class="m-query-hd">
    </div>
      
    </div>
</div>

<div id="table_data">
	<jsp:include page="blackList.jsp"></jsp:include>
</div>
<script type="text/javascript" src="/js/jquery.min.js"></script>
<script type="text/javascript" src="/js/bootstrap.js"></script>
<script type="text/javascript" src="/jsplugin/datepicker/WdatePicker.js"></script>
<script type="text/javascript" src="/js/jquery.min.js"></script>
<script type="text/javascript" src="/jsplugin/lhgdialog/lhgdialog.min.js?skin=iblue"></script>
<script type="text/javascript">
/*
 * 条件查询
 */
function find(currentPage){
	var url="/blacklist/find.action";
	var data = {
			"currentPage":currentPage,
			"map[customerId]":$("#customerId").val(),
			"map[description]":$("#description").val(),
			"map[typeId]":1
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
	  /**
	  * 跳转新增前的页面
	  */
     function addBlacklist(){
	
	  var str='<table><tr><td>客户编号<input type="text" name="customerId" id="customerId" value="1234567890" /></td></tr>'+
	               '<tr><td>IP地址<input type="text" name="ip" id="ip" value="192.168.1.102" /></td></tr>'+
	               '<tr><td>失效时间<input type="text" name="endDate" id="endDate"  value="2015-01-28 23:58:29"/></td></tr>'+
	               '<tr><td>阻止原因<input type="text" name="description" id="description" value="骂人"/></td></tr></table>';
	               
    	$.dialog({
		content:str,
		width: 900,height: 500,
		button: [
			        {
			            name: '确认',
			            callback: function () {
			               save(); 
			                return false;
			            },
			            focus: true
			        },
			        {
			            name: '取消'
			        }
			    ]
		});
    }
    /**
    *新增的方法
    */
    function save(){
	var url = "/blacklist/save.action";
	var data = {
		"customerId" : $("#customerId").val(),
		"ip" : $("#ip").val(),
		"description" : $("#description").val(),
		"enddate" : $("#endDate").val()
	};
	$.ajax({
		type : "post",
		url : url,
		data : data,
		dataType : "json",
		async:false,
		success : function(data) {
			if (data.result == 0) {
				alert(data.msg);
				$("#customerId").val('');
				$("#ip").val('');
				$("#endDate").val('');
				$("#description").val('');
				location.reload();
			} else {
				alert(data.msg);
			}
		},
		error : function(msg) {
			alert(data.msg);
		}
	});
  }
    /**
    *修改前的页面跳转
    */
    function toUpdate(blacklistId){
    	
    		$.dialog({content:'url:/blacklist/editBlack.action?blacklistId='+blacklistId,
    			width: 400,height: 500,
    			button: [
    				        {
    				            name: '确认',
    				            callback: function () {
    				                 update(); 
    				                return false;
    				            },
    				            focus: true
    				        },
    				        {
    				            name: '关闭'
    				        }
    				    ]
    			});
    	}

	/**
	* 删除
	*/
	function deleteList(){
		var id=$("input[name='ck']:checked").val();
		if(id != null){
			var choice=confirm("您确认要删除吗？", function() { }, null);
			if(choice){
				
				$("#blacklisttable input[name='ck']:checked").each(function(){
					var _checkbox = "<input type='hidden' name='ck' value='"+$(this).val()+"' />" ;
					$("#blacklisttable").append(_checkbox) ;
				});
				
				var url = "/blacklist/delete.action" ;
				$("#searchPage").val('${pager.pageNum}');
				$("#pageSearchForm").attr('action',url).attr('method','get') ;
				$("#pageSearchForm").submit();
			}
			
		}else{
			alert("请选择数据");
		}
	}
	//删除一个方法
    function del(id){
		var choice=confirm("您确认要删除吗？", function() { }, null);
		if(choice){
			var url = "/blacklist/delete.action"+id ;
			$("#searchPage").val('${pager.pageNum}');
			$("#pageSearchForm").attr('action',url).attr('method','get') ;
			$("#pageSearchForm").submit();
		}
		
	}
</script>
</body>
</html>