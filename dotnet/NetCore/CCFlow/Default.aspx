<%@ Page Language="C#" AutoEventWireup="true" Inherits="CCFlow.Default" CodeBehind="Default.aspx.cs" %>

<html>
<head>
    <title>ccbpm导航页</title>
    <link href="DataUser/Style/ccbpm.css" rel="stylesheet" type="text/css" />
    <style type="text/css">
        body {
        }
        li {
            font-size: 14px;
            margin: 5px;
            border-style: none;
            color: Gray;
        }
    </style>
    <base target="_blank" />
</head>

<body>

    <h3>驰骋BPM云服务系统</h3>
    <ul>
        <li><a href="Default.htm">前台首页 </a>
            管理员:13256732930  pass:123
        </li>
        <li><a href="/AdminSys/Login.htm">Admin Systeam </a></li>
    </ul>


    <h3>SAAS版登录</h3>
    <ul>
        <li><a href="Default.htm">PC前台登录  </a></li>
        <li><a href="/CCMobilePortal/Default.htm">移动前台登录  </a></li>
        <li>管理员:13370503398  pass:123</li>
    </ul>



    <h3>单组织版</h3>
    <ul>
        <li><a href="/WF/AppClassic/Login.htm">PC前台登录</a></li>
        <li><a href="/WF/Admin/Portal/Login.htm">管理员登录</a></li>
    </ul>


    <h3>极简开发</h3>
    <ul>
        <li><a href="/WF/AppClassic/Login.htm">PC前台登录</a></li>

        <li><a href="/WF/Admin/Portal/Login.htm">管理员登录</a></li>
        <li><a href="/FastMobilePortal/Login.htm">手机端登录</a></li>
    </ul>

</body>
</html>
