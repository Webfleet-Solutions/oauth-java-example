<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    String title = "3rd Party Home Page - Login Page";
%>
<%@include file="header.jsp" %>
<c:url value="/login" var="loginProcessingUrl"/>
<div class="container">

    <form class="form-signin" action="${loginProcessingUrl}" method="post">
        <fieldset>
            <legend>Please Login</legend>
            <!-- use param.error assuming FormLoginConfigurer#failureUrl contains the query parameter error -->
            <c:if test="${param.error != null}">
                <div>
                    Failed to login.
                    <c:if test="${SPRING_SECURITY_LAST_EXCEPTION != null}">
                        Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>
                    </c:if>
                </div>
            </c:if>
            <!-- the configured LogoutConfigurer#logoutSuccessUrl is /login?logout and contains the query param logout -->
            <c:if test="${param.logout != null}">
                <div>
                    You have been logged out.
                </div>
            </c:if>
            <p>
                <label for="username" class="sr-only">Username</label>
                <input type="text" class="form-control" id="username" placeholder="Username" name="username"/>
            </p>
            <p>
                <label for="password" class="sr-only">Password</label>
                <input type="password" class="form-control" placeholder="Password" id="password" name="password"/>
            </p>
            <div>
                <button type="submit" class="btn btn-lg btn-primary btn-block">Log in</button>
            </div>
        </fieldset>
    </form>
</div>
<%@include file="footer.jsp" %>