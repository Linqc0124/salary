<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>公司人员工资管理系统</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <style>
        body {
            padding-top: 56px;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            font-family: 'Inter', system-ui, sans-serif;
            background-color: #f9fafb;
        }

        .sidebar {
            position: fixed;
            top: 56px;
            bottom: 0;
            left: 0;
            z-index: 100;
            width: 240px;
            padding: 0;
            background-color: #f8fafc;
            border-right: 1px solid #e5e7eb;
            transition: width 0.3s ease;
        }

        .sidebar.collapsed {
            width: 64px;
        }

        .sidebar.collapsed .nav-link-text {
            display: none;
        }

        .sidebar-sticky {
            position: sticky;
            top: 0;
            height: calc(100vh - 56px);
            padding-top: 1rem;
            overflow-x: hidden;
            overflow-y: auto;
        }

        .nav-link {
            display: flex;
            align-items: center;
            font-weight: 500;
            color: #4b5563;
            padding: 0.875rem 1.25rem;
            border-radius: 0.5rem;
            margin: 0 0.5rem 0.25rem;
            transition: all 0.2s ease;
        }

        .nav-link:hover {
            color: #1e40af;
            background-color: #f3f4f6;
            transform: translateX(4px);
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
        }

        .nav-link.active {
            color: #1e40af;
            background-color: #dbeafe;
            box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
        }

        .nav-link i {
            margin-right: 1rem;
            font-size: 1.1rem;
            min-width: 24px;
            text-align: center;
        }

        main {
            margin-left: 240px;
            padding: 2rem;
            flex: 1;
            transition: margin-left 0.3s ease;
        }

        main.expanded {
            margin-left: 64px;
        }

        .navbar {
            background: linear-gradient(90deg, #3b82f6 0%, #60a5fa 100%);
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
        }

        .navbar-brand {
            padding: 0.75rem 1.5rem;
            font-size: 1.25rem;
            font-weight: 600;
            color: #fff;
            display: flex;
            align-items: center;
        }

        .navbar-brand i {
            margin-right: 0.5rem;
        }

        .dropdown-menu {
            right: 0;
            left: auto;
            border: 1px solid #e5e7eb;
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05), 0 4px 6px -2px rgba(0, 0, 0, 0.03);
            border-radius: 0.5rem;
        }

        .dropdown-item {
            padding: 0.5rem 1rem;
            transition: background-color 0.2s ease;
        }

        .dropdown-item:hover {
            background-color: #f3f4f6;
        }

        iframe {
            width: 100%;
            height: calc(100vh - 96px);
            border: none;
            border-radius: 0.5rem;
            background-color: #fff;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05), 0 1px 2px rgba(0, 0, 0, 0.03);
        }

        .toggle-sidebar {
            position: absolute;
            right: 1rem;
            top: 1rem;
            background: rgba(255, 255, 255, 0.2);
            border: none;
            color: #fff;
            width: 2rem;
            height: 2rem;
            border-radius: 0.5rem;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.2s ease;
        }

        .toggle-sidebar:hover {
            background: rgba(255, 255, 255, 0.3);
            transform: scale(1.05);
        }

        .user-info {
            display: flex;
            align-items: center;
            padding: 1rem 1.5rem;
            color: #1e40af;
            border-bottom: 1px solid #e5e7eb;
            margin-bottom: 1rem;
        }

        .user-info .user-avatar {
            width: 2.5rem;
            height: 2.5rem;
            background-color: #dbeafe;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 1rem;
        }

        .sidebar.collapsed .user-info {
            justify-content: center;
        }

        .sidebar.collapsed .user-info span {
            display: none;
        }

        .badge-notification {
            position: absolute;
            right: 1.5rem;
            background-color: #ef4444;
            color: white;
            font-size: 0.75rem;
            width: 1.25rem;
            height: 1.25rem;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
        }
    </style>
</head>
<body>
    <nav class="navbar navbar-dark fixed-top bg-dark flex-md-nowrap p-0 shadow">
        <a class="navbar-brand col-sm-3 col-md-2 mr-0" href="#">公司人员工资管理系统</a>
        <ul class="navbar-nav px-3">
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <i class="fas fa-user"></i> ${sessionScope.user.realName}
                </a>
                <div class="dropdown-menu" aria-labelledby="navbarDropdown">
<%--                    <a class="dropdown-item" href="${pageContext.request.contextPath}/user/password" target="mainFrame">修改密码</a>--%>
<%--                    <div class="dropdown-divider"></div>--%>
                    <a class="dropdown-item" href="${pageContext.request.contextPath}/logout">退出登录</a>
                </div>
            </li>
        </ul>
    </nav>

    <div class="container-fluid">
        <div class="row">
            <nav class="col-md-2 d-none d-md-block sidebar">
                <div class="sidebar-sticky">
                    <ul class="nav flex-column">
                        <li class="nav-item">
                            <a class="nav-link active" href="${pageContext.request.contextPath}/department" target="mainFrame">
                                <i class="fas fa-building"></i>部门管理
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/employee" target="mainFrame">
                                <i class="fas fa-users"></i>员工管理
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/salary/config" target="mainFrame">
                                <i class="fas fa-money-bill-wave"></i>工资配置
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/deduction" target="mainFrame">
                                <i class="fas fa-hand-holding-usd"></i>专项附加扣除
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/salary/record" target="mainFrame">
                                <i class="fas fa-file-invoice-dollar"></i>工资记录
                            </a>
                        </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/user" target="mainFrame">
                                    <i class="fas fa-user-cog"></i>用户管理
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/audit/log" target="mainFrame">
                                    <i class="fas fa-clipboard-list"></i>审计日志
                                </a>
                            </li>
                    </ul>
                </div>
            </nav>

            <main role="main" class="col-md-10 ml-sm-auto">
                <iframe name="mainFrame" src="${pageContext.request.contextPath}/department"></iframe>
            </main>
        </div>
    </div>

    <script src="/static/jquery.min.js"></script>
    <script src="/static/bootstrap.bundle.min.js"></script>
    <script>
        $(function() {
            // 导航菜单点击事件
            $('.nav-link').click(function() {
                $('.nav-link').removeClass('active');
                $(this).addClass('active');
            });
        });
    </script>
</body>
</html>
