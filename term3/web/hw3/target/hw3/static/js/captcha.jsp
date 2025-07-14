<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Captcha</title>
</head>
<body>
<img src="data:image/jpg;base64,<%=(String) request.getSession().getAttribute("captcha-image")%>"
     alt="Reload the page pls"/>
<div class="captcha-form">
    <form action="" method="post">
        <label for="captcha-form__user">Enter captcha:</label>
        <input name="actual-answer" id="captcha-form__user">
    </form>
</div>
</body>
</html>
