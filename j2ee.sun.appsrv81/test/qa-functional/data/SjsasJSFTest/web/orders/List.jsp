<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>List Orders</title>
    </head>
    <body>
        <f:view>
            <h:messages errorStyle="color: red" infoStyle="color: green" layout="table"/>
            <h1>Listing Orderss</h1>
            <h:form>
                <h:commandLink action="#{orders.createSetup}" value="New Orders"/>
                <br>
                <a href="/SjsasJSFTest/index.jsp">Back to index</a>
                <br>
                <h:outputText value="Item #{orders.firstItem + 1}..#{orders.lastItem} of #{orders.itemCount}"/>&nbsp;
                <h:commandLink action="#{orders.prev}" value="Previous #{orders.batchSize}" rendered="#{orders.firstItem >= orders.batchSize}"/>&nbsp;
                <h:commandLink action="#{orders.next}" value="Next #{orders.batchSize}" rendered="#{orders.lastItem + orders.batchSize <= orders.itemCount}"/>&nbsp;
                <h:commandLink action="#{orders.next}" value="Remaining #{orders.itemCount - orders.lastItem}"
                rendered="#{orders.lastItem < orders.itemCount && orders.lastItem + orders.batchSize > orders.itemCount}"/><h:dataTable value='#{orders.orderss}' var='item' border="1" cellpadding="2" cellspacing="0">
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="OrderNum"/>
                        </f:facet>
                        <h:commandLink action="#{orders.detailSetup}">
                            <f:param name="orderNum" value="#{item.orderNum}"/>
                            <h:outputText value="#{item.orderNum}"/>
                        </h:commandLink>
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="Quantity"/>
                        </f:facet>
                        <h:outputText value="#{item.quantity}"/>
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="ShippingCost"/>
                        </f:facet>
                        <h:outputText value="#{item.shippingCost}"/>
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="SalesDate"/>
                        </f:facet>
                        <h:outputText value="#{item.salesDate}">
                            <f:convertDateTime type="DATE" pattern="MM/dd/yyyy" />
                        </h:outputText>
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="ShippingDate"/>
                        </f:facet>
                        <h:outputText value="#{item.shippingDate}">
                            <f:convertDateTime type="DATE" pattern="MM/dd/yyyy" />
                        </h:outputText>
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="FreightCompany"/>
                        </f:facet>
                        <h:outputText value="#{item.freightCompany}"/>
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="CustomerId"/>
                        </f:facet>
                        <h:commandLink action="#{customer.detailSetup}">
                            <f:param name="customerId" value="#{item.customerId.customerId}"/>
                            <h:outputText value="#{item.customerId.customerId}"/>
                        </h:commandLink>
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="ProductId"/>
                        </f:facet>
                        <h:commandLink action="#{product.detailSetup}">
                            <f:param name="productId" value="#{item.productId.productId}"/>
                            <h:outputText value="#{item.productId.productId}"/>
                        </h:commandLink>
                    </h:column>
                    <h:column>
                        <h:commandLink value="Destroy" action="#{orders.destroy}">
                            <f:param name="orderNum" value="#{item.orderNum}"/>
                        </h:commandLink>
                        <h:outputText value=" "/>
                        <h:commandLink value="Edit" action="#{orders.editSetup}">
                            <f:param name="orderNum" value="#{item.orderNum}"/>
                        </h:commandLink>
                    </h:column>
                </h:dataTable>
            </h:form>
        </f:view>
    </body>
</html>
