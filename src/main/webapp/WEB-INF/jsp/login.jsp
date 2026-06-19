<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>公司人员工资管理系统 - 登录</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.0/css/bootstrap.min.css">
    <style>
        :root {
            --primary-gradient: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
            --success-gradient: linear-gradient(135deg, #10b981 0%, #059669 100%);
            --error-gradient: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
            --neutral-soft: #f8fafc;
            --shadow-sm: 0 2px 4px rgba(0, 0, 0, 0.05);
            --shadow-md: 0 8px 16px rgba(0, 0, 0, 0.1);
            --transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }

        body {
            background: url("../../image/P-1.png") !important;
            background-size: cover;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
            position: relative;
            overflow: hidden;
        }

        body::after {
            content: '';
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(255, 255, 255, 0.7);
            z-index: 1;
            backdrop-filter: blur(5px);
        }

        .login-container {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 1.5rem;
            box-shadow: var(--shadow-md);
            width: 100%;
            max-width: 420px;
            padding: 2.5rem;
            position: relative;
            z-index: 2;
            backdrop-filter: blur(10px);
            transition: var(--transition);
            animation: floatUp 0.6s ease-out;
        }

        .login-title {
            font-size: 1.875rem;
            font-weight: 600;
            color: #1e293b;
            text-align: center;
            margin-bottom: 2rem;
            position: relative;
            padding-bottom: 1rem;
        }

        .login-title::after {
            content: '';
            position: absolute;
            bottom: 0;
            left: 50%;
            transform: translateX(-50%);
            width: 60px;
            height: 3px;
            background: var(--primary-gradient);
            border-radius: 2px;
        }

        .form-group {
            margin-bottom: 1.75rem;
            position: relative;
        }

        .form-label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
            color: #475569;
            transition: var(--transition);
        }

        .form-control {
            border: 2px solid #e2e8f0;
            border-radius: 0.875rem;
            padding: 0.875rem 1.25rem;
            width: 100%;
            transition: var(--transition);
            font-size: 1rem;
            background: rgba(255, 255, 255, 0.8);
        }

        .form-control:focus {
            border-color: #6366f1;
            box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
            outline: none;
        }

        .form-control:focus + .form-label {
            color: #6366f1;
        }

        .btn-login {
            background: var(--primary-gradient);
            border: none;
            border-radius: 0.875rem;
            color: white;
            font-weight: 600;
            padding: 0.875rem 1.5rem;
            width: 100%;
            transition: var(--transition);
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
            font-size: 1.05rem;
        }

        .btn-login:hover {
            background: linear-gradient(135deg, #4f46e5 0%, #4338ca 100%);
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(79, 70, 229, 0.3);
        }

        .btn-login:active {
            transform: translateY(0);
        }

        .login-error {
            background: var(--error-gradient);
            color: white;
            padding: 0.75rem 1.5rem;
            border-radius: 0.75rem;
            margin-bottom: 1.5rem;
            display: none;
            animation: fadeIn 0.3s ease-out;
        }

        /* 动画 */
        @keyframes floatUp {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }

        /* 响应式调整 */
        @media (max-width: 480px) {
            .login-container {
                padding: 1.5rem;
                margin: 1rem;
            }

            .login-title {
                font-size: 1.5rem;
            }

            .form-control {
                padding: 0.75rem 1rem;
            }

            .btn-login {
                padding: 0.75rem 1rem;
            }
        }
    </style>
</head>
<body>
    <div class="login-container">
        <h3 class="login-title">公司人员工资管理系统</h3>
        <div id="loginError" class="login-error" style="display: none;"></div>
        <form id="loginForm" method="post">
            <div class="form-group">
                <label for="username">用户名</label>
                <input type="text" class="form-control" id="username" name="username" required>
            </div>
            <div class="form-group">
                <label for="password">密码</label>
                <input type="password" class="form-control" id="password" name="password" required>
            </div>
            <button type="submit" class="btn btn-primary btn-login">登录</button>
        </form>
    </div>

    <script src="/static/jquery.min.js"></script>
    <script src="/static/bootstrap.bundle.min.js"></script>
    <script>
        $(function() {
            // 获取URL参数
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.has('timeout')) {
                $('#loginError').text('会话已超时，请重新登录').show();
            }

            // 登录表单提交
            $('#loginForm').submit(function(e) {
                e.preventDefault();

                $.ajax({
                    url: '${pageContext.request.contextPath}/login',
                    type: 'POST',
                    data: $(this).serialize(),
                    dataType: 'json',
                    success: function(res) {
                        if (res.code === 200) {
                            window.location.href = '${pageContext.request.contextPath}/index';
                        } else {
                            $('#loginError').text(res.message).show();
                        }
                    },
                    error: function() {
                        $('#loginError').text('系统错误，请稍后再试').show();
                    }
                });
            });
        });
    </script>
</body>
</html>
