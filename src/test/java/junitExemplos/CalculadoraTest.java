package junitExemplos;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CalculadoraTest {


    @Test
    public void testSoma(){
        Calculadora cal = new Calculadora();
        int resultado = cal.soma(3,5);
        Assertions.assertEquals(8,resultado,"A soma de 3 e 5 deve ser igual a 8");
    }
}
