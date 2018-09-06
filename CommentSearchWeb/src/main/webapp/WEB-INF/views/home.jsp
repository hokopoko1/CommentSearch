<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta charset="utf-8">
	<title>Home</title>
	
	<link href="/resources/vendors/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
	    <!-- Font Awesome -->
    <link href="/resources/vendors/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <!-- NProgress -->
    <link href="/resources/vendors/nprogress/nprogress.css" rel="stylesheet">
    <!-- iCheck -->
    <link href="/resources/vendors/iCheck/skins/flat/green.css" rel="stylesheet">
    <!-- bootstrap-wysiwyg -->
    <link href="/resources/vendors/google-code-prettify/bin/prettify.min.css" rel="stylesheet">
    <!-- Select2 -->
    <link href="/resources/vendors/select2/dist/css/select2.min.css" rel="stylesheet">
    <!-- Switchery -->
    <link href="/resources/vendors/switchery/dist/switchery.min.css" rel="stylesheet">
    <!-- starrr -->
    <link href="/resources/vendors/starrr/dist/starrr.css" rel="stylesheet">
    <!-- bootstrap-daterangepicker -->
    <link href="/resources/vendors/bootstrap-daterangepicker/daterangepicker.css" rel="stylesheet">
    
    <link href="/resources/vendors/datatables.net-bs/css/dataTables.bootstrap.min.css" rel="stylesheet">

    <!-- Custom Theme Style -->
    <link href="/resources/build/css/custom.min.css" rel="stylesheet">
	
	</head>

	
	<script type="text/javascript">
	
	function search(){
		var keyword = document.getElementById("search").value;
		
		var uri="/searchVideo";  
	    var params="?keyword="+keyword;  
	    
	    var oTable = $('#dataTables').DataTable({
	    	"processing" : true,
	    	"serverSide" : true,
	    	destroy : true,
	    	"bStateSave" : true,
	    	ajax : {
	    		type:"POST",  
	 			url:uri + params,
	    	},
	    	columns: [
	    		/*
	    		{ 
	    			title: "videoId", data:"videoId", "visible" : false,
	    			"fnCreatedCell" : function(nTd, sData, oData, iRow, iCol){
	    				$(nTd).html(
	    					makeVideoId(sData)		
	    				);}
	    		},*/
	    		{
	    			title: "Video", data:"thumbnail", "fnCreatedCell" : function(nTd, sData, oData, iRow, iCol){
	    				$(nTd).html(
	    					makeThumnail(sData)		
	    				);}
	    		},
	    		{ title: "title", data:"title" }
	    	]
	    	
	    });

	}
	
	function csearch(){
		var keyword = document.getElementById("search").value;
		
		var uri="/csearchVideo";  
	    var params="?keyword="+keyword;  
	    
	    var oTable = $('#dataTables').DataTable({
	    	"processing" : true,
	    	"serverSide" : true,
	    	destroy : true,
	    	"bStateSave" : true,
	    	ajax : {
	    		type:"POST",  
	 			url:uri + params,
	    	},
	    	columns: [
	    		/*
	    		{ 
	    			title: "videoId", data:"videoId", "visible" : false,
	    			"fnCreatedCell" : function(nTd, sData, oData, iRow, iCol){
	    				$(nTd).html(
	    					makeVideoId(sData)		
	    				);}
	    		},*/
	    		{
	    			title: "Video", data:"thumbnail", "fnCreatedCell" : function(nTd, sData, oData, iRow, iCol){
	    				$(nTd).html(
	    					makeThumnail(sData)		
	    				);}
	    		},
	    		{ title: "title", data:"title" }
	    	]
	    	
	    });

	}
	
	function makeVideoId(videoId){
		seletedVideoId = videoId;
		
		return "<a href=\"javascript:comment()\">"+ videoId+ "</a>";
	}
	
	function makeThumnail(url){
		return "<img style='width: 100%; display: block;' src=" + url + ">";
	}
	
	var seletedVideoId
	function comment(){
		//var videoId = document.getElementById("comment").value;
		
		var uri="/getComment";  
	    var params="?videoId="+seletedVideoId;  
	    
	    var oTable = $('#commentTables').DataTable({
	    	"processing" : true,
	    	"serverSide" : true,
	    	destroy : true,
	    	"bStateSave" : true,
	    	ajax : {
	    		type:"POST",  
	 			url:uri + params,
	    	},
	    	columns: [
	    		{ title: "Time", data:"time" },
	    		{ title: "Author", data:"author" },
	    		{ title: "Comment", data:"comment" }
	    	]
	    	
	    });
	    /*
	    $.ajax({      
	        type:"POST",  
	        url:url,      
	        data:params,
	        contentType: "application/x-www-form-urlencoded; charset=UTF-8", 
	        success:function(input){
	            alert(input);
	            
	        },   
	        error:function(e){  
	            alert(e.responseText);  
	        }  
	    });
	    */
	}

	</script>
	
	
<body class="nav-md">

	<div class="container body">
		<div class="main_container">
			<div class="col-md-3 left_col">
				<div class="left_col scroll-view">
					<div class="navbar nav_title" style="border: 0;">
						<a href="index.html" class="site_title"><i class="fa fa-paw"></i>
							<span>Comment Search</span></a>
					</div>

					<div class="clearfix"></div>

					<!-- menu profile quick info -->
					<div class="profile clearfix">
						<div class="profile_info">
							<span>Welcome,</span>
							<h2>Minho Song</h2>
						</div>
					</div>
					<!-- /menu profile quick info -->

					<br />

					<!-- sidebar menu -->
					<div id="sidebar-menu"
						class="main_menu_side hidden-print main_menu">
						<div class="menu_section">
							<h3>General</h3>
							<ul class="nav side-menu">
								<li><a><i class="fa fa-home"></i> Home <span class="fa fa-chevron-down"></span></a>
									<ul class="nav child_menu">
										<li><a href="index.html">Dashboard</a></li>
									</ul>
								</li>
							</ul>
						</div>
					</div>
					<!-- /sidebar menu -->
				</div>
			</div>

			<!-- top navigation -->
			<div class="top_nav">
				<div class="nav_menu">
					<nav>
						<div class="nav toggle">
							<a id="menu_toggle"><i class="fa fa-bars"></i></a>
						</div>

						<ul class="nav navbar-nav navbar-right">
							<li class=""><a href="javascript:;" class="user-profile dropdown-toggle" data-toggle="dropdown" aria-expanded="false"> <span class=" fa fa-angle-down"></span>
							</a></li>
						</ul>
					</nav>
				</div>
			</div>
			<!-- /top navigation -->

			<!-- page content -->
			<div class="right_col" role="main">
				<div class="">
					<div class="page-title">
						<div class="title_left">
							<h3>Comment Search</h3>
						</div>
					</div>
					<div class="clearfix"></div>
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">
							<div class="x_panel">
								<div class="x_title">
									<h2>
										Form Design <small>different form elements</small>
									</h2>
									<ul class="nav navbar-right panel_toolbox">
										<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
										<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
											<i class="fa fa-wrench"></i></a></li>
										<li><a class="close-link"><i class="fa fa-close"></i></a>
										</li>
									</ul>
									<div class="clearfix"></div>
								</div>
								<div class="x_content">
									<br />
									<label class="control-label col-md-3 col-sm-3 col-xs-12" for="first-name">Keyword <span class="required">*</span>
			                        </label>
			                        <div class="col-md-6 col-sm-6 col-xs-12">
			                          <input type="text" id="search" >
			                        </div>
									<button type="submit" onclick="search();" class="btn btn-success">Submit</button>
								</div>
								<!-- 
								<div class="x_content">
									<br />
									<label class="control-label col-md-3 col-sm-3 col-xs-12" for="first-name">videoId(comment) <span class="required">*</span>
			                        </label>
			                        <div class="col-md-6 col-sm-6 col-xs-12">
			                          <input type="text" id="comment" >
			                        </div>
									<button type="submit" onclick="comment();" class="btn btn-success">Submit</button>
								</div>
								 -->
							</div>
						</div>
						<div class="col-md-6 col-sm-6 col-xs-12">
							<div class="x_panel">
								<div class="x_title">
									<ul class="nav navbar-right panel_toolbox">
										<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
									</ul>
								</div>
								<div class="x_content">
									<table class="table table-striped table-bordered" id="dataTables"></table>
								</div>
							</div>
						</div>
						<div class="col-md-6 col-sm-6 col-xs-12">
							<div class="x_panel">
								<div class="x_title">
									<ul class="nav navbar-right panel_toolbox">
										<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
									</ul>
								</div>
								<div class="x_content">
									<table class="table table-striped table-bordered" id="csearchTables"></table>
								</div>
							</div>
						</div>
						<div class="col-md-12 col-sm-12 col-xs-12">
							<div class="x_panel">
								<div class="x_title">
									<ul class="nav navbar-right panel_toolbox">
										<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
									</ul>
								</div>
								<div class="x_content">
									<table class="table table-striped table-bordered" id="commentTables"></table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- /page content -->

			<!-- footer content -->
			<footer>
				<div class="pull-right">
					Gentelella - Bootstrap Admin Template by <a
						href="https://colorlib.com">Colorlib</a>
				</div>
				<div class="clearfix"></div>
			</footer>
			<!-- /footer content -->
		</div>
	</div>

	<!-- jQuery -->
	<script src="/resources/vendors/jquery/dist/jquery.min.js"></script>
	<!-- Bootstrap -->
	<script src="/resources/vendors/bootstrap/dist/js/bootstrap.min.js"></script>
    <!-- FastClick -->
    <script src="/resources/vendors/fastclick/lib/fastclick.js"></script>
    <!-- NProgress -->
    <script src="/resources/vendors/nprogress/nprogress.js"></script>
    <!-- bootstrap-progressbar -->
    <script src="/resources/vendors/bootstrap-progressbar/bootstrap-progressbar.min.js"></script>
    <!-- iCheck -->
    <script src="/resources/vendors/iCheck/icheck.min.js"></script>
    <!-- bootstrap-daterangepicker -->
    <script src="/resources/vendors/moment/min/moment.min.js"></script>
    <script src="/resources/vendors/bootstrap-daterangepicker/daterangepicker.js"></script>
    <!-- bootstrap-wysiwyg -->
    <script src="/resources/vendors/bootstrap-wysiwyg/js/bootstrap-wysiwyg.min.js"></script>
    <script src="/resources/vendors/jquery.hotkeys/jquery.hotkeys.js"></script>
    <script src="/resources/vendors/google-code-prettify/src/prettify.js"></script>
    <!-- jQuery Tags Input -->
    <script src="/resources/vendors/jquery.tagsinput/src/jquery.tagsinput.js"></script>
    <!-- Switchery -->
    <script src="/resources/vendors/switchery/dist/switchery.min.js"></script>
    <!-- Select2 -->
    <script src="/resources/vendors/select2/dist/js/select2.full.min.js"></script>
    <!-- Parsley -->
    <script src="/resources/vendors/parsleyjs/dist/parsley.min.js"></script>
    <!-- Autosize -->
    <script src="/resources/vendors/autosize/dist/autosize.min.js"></script>
    <!-- jQuery autocomplete -->
    <script src="/resources/vendors/devbridge-autocomplete/dist/jquery.autocomplete.min.js"></script>
    <!-- starrr -->
    <script src="/resources/vendors/starrr/dist/starrr.js"></script>
    <!-- Custom Theme Scripts -->
    <script src="/resources/build/js/custom.min.js"></script>
    <script src="/resources/vendors/datatables.net/js/jquery.dataTables.min.js"></script>
    <script src="/resources/vendors/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
</body>

</html>
