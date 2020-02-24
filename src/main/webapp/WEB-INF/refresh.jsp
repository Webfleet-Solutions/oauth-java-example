<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String title = "OAuth Java Example";
%>
<%@include file="common/header.jsp" %>
<%@include file="common/menu.jsp" %>
<main role="main" class="container d-flex vh-90">
    <div class="row align-self-center w-100">
        <div class="col-8 mx-auto">
            <div class="jumbotron">
                <h2>Welcome <sec:authentication property="principal.username"/></h2>
                <p class="lead">This is a sample web application access Webfleet Solutions APIs</p>
                <hr/>
                <p>Please use the button below to link to a customer account.</p>
                <p class="small">You will be required to login with customer credentials.</p>
                <c:url value="<%=KnownUrls.SERVICE%>" var="service"/>
                <a class="btn btn-primary btn-lg" href="${service}" role="button">Link account</a>
            </div>
        </div>
    </div>
</main>
<%@include file="common/footer.jsp" %>