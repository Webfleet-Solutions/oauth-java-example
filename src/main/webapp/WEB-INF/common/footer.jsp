<footer class="footer">
    <div class="container float-left">
        <c:url value="/static/images/wfs-abc.svg" var="imageUrl"/>
        <img src="${imageUrl}" class="" width="111" height="40" alt="wfs abc"/>
    </div>
</footer>
<!-- Optional JavaScript -->
<!-- jQuery first, then Bootstrap JS -->
<c:url value="/static/js/jquery-3.4.1.min.js" var="jquery"/>
<script src="${jquery}"></script>
<c:url value="/static/bootstrap-4.4.1-dist/js/bootstrap.min.js" var="bootstrapjs"/>
<script src="${bootstrapjs}"></script>
</body>
</html>