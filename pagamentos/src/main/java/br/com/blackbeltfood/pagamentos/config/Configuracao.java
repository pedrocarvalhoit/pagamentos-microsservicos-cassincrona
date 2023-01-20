package br.com.blackbeltfood.pagamentos.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Configuracao {

    //Configurando o bean do ModelMapper
    @Bean
    public ModelMapper obterModelMapper(){
        return new ModelMapper();
    }

}
