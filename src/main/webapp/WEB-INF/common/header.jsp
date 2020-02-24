<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <!-- Required bootstrap meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <!-- END Required bootstrap meta tags -->
    <title><%=title%>
    </title>
    <!-- Latest compiled and minified CSS -->
    <c:url value="/static/bootstrap-4.4.1-dist/css/bootstrap.min.css" var="bootstrapcss"/>
    <link rel="stylesheet" href="${bootstrapcss}">

    <style type="text/css">
        /* Sticky footer styles
    -------------------------------------------------- */
        html {
            position: relative;
            min-height: 100%;
        }

        body {
            /* Margin bottom by footer height */
            margin-bottom: 60px;
            height: 100%;
        }

        .footer {
            position: absolute;
            bottom: 0;
            width: 100%;
            /* Set the fixed height of the footer here */
            height: 60px;
            line-height: 60px; /* Vertically center the text there */
            background-color: #f5f5f5;
        }


        /* Custom page CSS
        -------------------------------------------------- */
        /* Not required for template or sticky footer method. */

        body > .container {
            padding: 60px 15px 0;
        }

        .footer > .container {
            padding-right: 15px;
            padding-left: 15px;
        }

        .vh-90 {
            height: 90vh !important;
        }

    </style>
    <c:url value="/static/highlight/styles/default.css" var="highlightcss"/>
    <link rel="stylesheet"
          href="${highlightcss}">
    <c:url value="/static/highlight/highlight.pack.js" var="highlightjs"/>
    <script src="${highlightjs}"></script>
    <script>hljs.initHighlightingOnLoad();</script>
</head>
<body>