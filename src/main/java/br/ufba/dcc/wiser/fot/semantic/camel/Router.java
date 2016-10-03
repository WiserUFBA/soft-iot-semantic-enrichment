package br.ufba.dcc.wiser.fot.semantic.camel;

import org.apache.camel.builder.RouteBuilder;

public class Router extends RouteBuilder {

	private String time;

   @Override
   public void configure() throws Exception {
       // Criando um rota com o componente timer do camel. Para mais componentes, 
       // visitar http://camel.apache.org/components.html
       // Nesse caso estou pegando dados do método transform e enviando para 
       // o updateInventory como String
       from ("timer://myTimer?fixedRate=true&period="+ time)
               .beanRef("busObj", "getServicesAndBuildMsgs");
   }

public void setTime(String time) {
	this.time = time;
}
   
   
}


