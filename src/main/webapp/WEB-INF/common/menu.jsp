<%@ page import="com.webfleet.oauth.common.KnownUrls" %>
<header>
    <nav class="navbar navbar-expand-md navbar-dark fixed-top bg-dark">
        <ul class="navbar-nav mr-auto">
            <sec:authorize access="isAuthenticated()">
                <li><span class="navbar-text">Welcome <sec:authentication property="principal.username"/></span></li>
            </sec:authorize>
        </ul>
        <ul class="navbar-nav ml-auto">
            <sec:authorize access="isAuthenticated()">

                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button"
                       data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        Options
                    </a>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="navbarDropdown">
                        <c:forEach var="entry" items="${menu}">
                            <c:url value="${entry.key}" var="linkUrl"/>
                            <a class="dropdown-item" href="<c:out value="${linkUrl}"/>"><c:out
                                    value="${entry.value}"/></a>
                        </c:forEach>
                    </div>
                </li>
                <c:url value="<%=KnownUrls.LOGOUT%>" var="logoutUrl"/>
                <li class="nav-item nav-"><a class="nav-link" href="${logoutUrl}">Logout</a></li>
            </sec:authorize>
            <sec:authorize access="isAnonymous()">
                <c:url value="<%=KnownUrls.LOGIN%>" var="loginUrl"/>
                <li class="nav-item"><a class="nav-link" href="${loginUrl}">Login</a></li>
            </sec:authorize>
        </ul>
    </nav>
</header>