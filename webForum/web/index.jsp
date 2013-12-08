<%-- 
    Document   : index
    Created on : 11-nov-2013, 15.10.39
    Author     : Giulian
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
    <head>
        <title></title>
        <link href="//netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css" rel="stylesheet">
        <style>
            div.custom {
                margin:8% auto;
                text-align:left;
                padding:15px;
                width: 30%;
                height: 60%;

                padding: 30px;
            }
            input {
                margin:5px;
            }
            body, html {
                background:#eee;
                height:72%;
                text-align:center;
            }
        </style><!--Stile minimo per centrare il div (non è proprio corretto così, ma vabbè) e per colorare lo sfondo della pagina di grigio-->
    </head>

    <body>
        <div class="custom panel" >
            <form class="form-signin" method ="post" action="loginSrvlt">
                <h2 class="form-signin-heading" style="margin:3% auto; text-align:left">webForum sign in</h2>
                <input type="text" name="username" class="form-control" placeholder="Email address" required="" autofocus="">
                <input type="password" name="password" class="form-control" placeholder="Password" required="">
                <button class="btn btn-lg btn-primary btn-block" style="margin:5% auto; width:20%; height:3%" type="submit">Sign in</button>
            </form>
        </div>
        <script src="http://code.jquery.com/jquery-latest.js"></script>
        <script src="//netdna.bootstrapcdn.com/bootstrap/3.0.2/js/bootstrap.min.js"></script>
    </body>
</html>
