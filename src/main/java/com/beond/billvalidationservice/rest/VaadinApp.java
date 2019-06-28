package com.beond.billvalidationservice.rest;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.AuthenticationController;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;

@Route("test")
public class VaadinApp extends Composite<Div> {

   private static final Logger LOG = LoggerFactory.getLogger(VaadinApp.class);

   private static final long serialVersionUID = 5079691319951883590L;

   private final Button         btnClickMe   = new Button("click me");
   private final Span           lbClickCount = new Span("0");
   private final VerticalLayout layout       = new VerticalLayout(btnClickMe, lbClickCount);

   public VaadinApp() {
      btnClickMe.addClickListener(event -> {

         HttpServletRequest request = ((VaadinServletRequest) VaadinService.getCurrentRequest()).getHttpServletRequest();
         String domain = request.getServletContext().getInitParameter("com.auth0.domain");
         LOG.warn("here I am!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!s {}", domain);
         AuthenticationController authenticationController = AuthenticationControllerProvider.getInstance(request.getServletContext());
         String url = authenticationController
               .buildAuthorizeUrl(request, "http://localhost:8080/rest/time/now")
               .withScope("openid email profile").build();
         LOG.warn("URL: {}", url);


         btnClickMe.getUI().ifPresent(ui -> {
            ui.getPage().executeJavaScript("window.location.href='" + url + "'");
         });
      });
      //set the main Component
      System.out.println("setting now the main ui content..");
      Anchor anchor = new Anchor("https://www.google.com", "uncle google");
      layout.add(anchor);
      getContent().add(layout);

   }

}
