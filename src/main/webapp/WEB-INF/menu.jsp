<section class="menu-container">
    <nav class="navbar navbar-default">
        <ul class="nav navbar-nav navbar-right">
            <sec:authorize access="isAuthenticated()">
                <c:forEach var="entry" items="${menu}">
                    <c:url value="${entry.key}" var="linkUrl"/>
                    <li><a href="<c:out value="${linkUrl}"/>"><c:out value="${entry.value}"/></a></li>
                </c:forEach>
                <c:url value="/logout" var="logoutUrl"/>
                <li><a href="${logoutUrl}">Logout</a></li>
            </sec:authorize>
            <sec:authorize access="isAnonymous()">
                <c:url value="/login" var="loginUrl"/>
                <li><a href="${loginUrl}">Login</a></li>
            </sec:authorize>
        </ul>
    </nav>
</section>
<section class="header-container">
    <sec:authorize access="isAuthenticated()">
        <p>Welcome "<sec:authentication property="principal.username"/>"</p>
    </sec:authorize>
</section>