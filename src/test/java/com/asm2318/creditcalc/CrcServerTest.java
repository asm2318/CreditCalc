/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asm2318.creditcalc;

import static com.asm2318.creditcalc.CrcModel.importPath;
import static com.asm2318.creditcalc.CrcModel.serialPath;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.mockito.Matchers;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;


public class CrcServerTest {


String importPath = ".\\_test_import\\";
String exportPath = ".\\_received\\";
String serialPath = ".\\_serialized\\";
String serverPath = ".\\_export\\";
static int port=8080;
static String ipAddress="127.0.0.1";

    public CrcServerTest() {
        
    }
    
    /*@BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }*/
    
    @Test // Тест № 1. Сервер - запуск и установка соединения.
    public void testAServerRun(){
        System.out.println("Тест № 1");
        try{
            startServer();
            SocketChannel socket = SocketChannel.open(new InetSocketAddress(ipAddress, port));
            socket.socket().close();
            System.out.println("Тест № 1 завершен: сервер работает");
        }catch(Exception e){
            fail("Ошибка теста № 1 при запуске сервера // "+e);}
    }
    
    @Test //Тест № 2. Клиент - проверка создания каталогов, необходимых для обработки файлов.
    public void testBClientStartWork(){
        System.out.println("Тест № 2");
        try{
        CrcModel firstClient = spy(new CrcModel());
        CrcController controller = mock(CrcController.class);
        Mockito.doNothing().when(firstClient).importer(Matchers.any(File.class));
        firstClient.startWork(controller);
        exportPath = firstClient.exportPath;
        serialPath = firstClient.serialPath;
            if (firstClient.importDir.exists() && firstClient.exportDir.exists() && firstClient.serializeDir.exists()){
                System.out.println("Тест № 2 завершен: каталоги созданы");
            }else{
                fail("Ошибка теста № 2 при инициализации каталогов клиента"); 
            }
        }catch (Exception e){
            fail("Ошибка теста № 2 при инициализации каталогов клиента // "+e); 
        }
    }
    
    @Test //Тест № 3. Клиент - проверка чтения исходных файлов.
    public void testCClientReadFiles(){
        System.out.println("Тест № 3");
        try{
            createThreeTestFiles();
            CrcModel firstClient = spy(new CrcModel());
            Mockito.doNothing().when(firstClient).readLists(Matchers.any(Boolean.class),Matchers.any(CopyOnWriteArrayList.class));
            firstClient.importer(new File(importPath));
            CopyOnWriteArrayList n3Result = firstClient.lists;
            assertEquals(n3Result.size(), 7);
            CopyOnWriteArrayList<String> listID = (CopyOnWriteArrayList<String>)n3Result.get(0);
            CopyOnWriteArrayList<String> listLoan = (CopyOnWriteArrayList<String>)n3Result.get(1);
            CopyOnWriteArrayList<String> listPercent = (CopyOnWriteArrayList<String>)n3Result.get(2);
            CopyOnWriteArrayList<String> listPayment = (CopyOnWriteArrayList<String>)n3Result.get(3);
            CopyOnWriteArrayList<String> listAnnu = (CopyOnWriteArrayList<String>)n3Result.get(4);
            CopyOnWriteArrayList<String> listTerm = (CopyOnWriteArrayList<String>)n3Result.get(5);
            CopyOnWriteArrayList<String> listPaydate = (CopyOnWriteArrayList<String>)n3Result.get(6);

                assertEquals(listID.size(), listPaydate.size(), 6);
                assertEquals(listLoan.size(), listPercent.size(), 6);
                assertEquals(listPayment.size(), listTerm.size(), 6);
                assertEquals(listAnnu.size(), 6);
                
                boolean containsAll=true;
                int i=listID.indexOf("test0101");
                if (!listLoan.get(i).equals("1000000.00")||!listPercent.get(i).equals("16.50")||!listPayment.get(i).equals("0.00")||!listAnnu.get(i).equals("annu")||
                        !listTerm.get(i).equals("36")||!listPaydate.get(i).equals("12.12.2018")) containsAll=false;
                i=listID.indexOf("test0102");
                if (!listLoan.get(i).equals("2000000.00")||!listPercent.get(i).equals("15.00")||!listPayment.get(i).equals("45000.00")||!listAnnu.get(i).equals("diff")||
                        !listTerm.get(i).equals("42")||!listPaydate.get(i).equals("31.03.2019")) containsAll=false;
                i=listID.indexOf("test0103");
                if (!listLoan.get(i).equals("500000.00")||!listPercent.get(i).equals("17.25")||!listPayment.get(i).equals("32501.65")||!listAnnu.get(i).equals("annu")||
                        !listTerm.get(i).equals("15")||!listPaydate.get(i).equals("30.11.2019")) containsAll=false;
                i=listID.indexOf("test0201");
                if (!listLoan.get(i).equals("1000000.00")||!listPercent.get(i).equals("16.50")||!listPayment.get(i).equals("0.00")||!listAnnu.get(i).equals("annu")||
                        !listTerm.get(i).equals("36")||!listPaydate.get(i).equals("12.12.2018")) containsAll=false;
                i=listID.indexOf("test0202");
                if (!listLoan.get(i).equals("100000.00")||!listPercent.get(i).equals("10.00")||!listPayment.get(i).equals("0.00")||!listAnnu.get(i).equals("diff")||
                        !listTerm.get(i).equals("6")||!listPaydate.get(i).equals("15.10.2019")) containsAll=false;
                i=listID.indexOf("test0203");
                if (!listLoan.get(i).equals("100000.00")||!listPercent.get(i).equals("10.00")||!listPayment.get(i).equals("0.00")||!listAnnu.get(i).equals("annu")||
                        !listTerm.get(i).equals("6")||!listPaydate.get(i).equals("01.11.2019")) containsAll=false;
                
                if(containsAll){
                    System.out.println("Тест № 3 завершен: исходные файлы прочитаны корректно");
                }else{
                    fail("Ошибка теста № 3 при чтении исходных файлов");
                }
        }catch (Exception e){
            fail("Ошибка теста № 3 при чтении исходных файлов // "+e);
        }
    }
    
    @Test //Тест № 4. Клиент - обработка первой половины списка без сериализации.
    public void testDClientReadLists(){

        System.out.println("Тест № 4");
        try{
            CrcModel firstClient = spy(new CrcModel());
            firstClient.setPort(port, ipAddress);
            CrcController controller = mock(CrcController.class);
            firstClient.controller = controller;
            firstClient.exportPath = exportPath;
            File export = new File(exportPath);
            if(!export.exists()) export.mkdir();
            Mockito.doNothing().when(firstClient).serialize(Matchers.any(CopyOnWriteArrayList.class));
            Mockito.doNothing().when(firstClient).deserialize();
            
            //четное количество
                try{
                    CopyOnWriteArrayList<CopyOnWriteArrayList> lists = filledList(4,1,2);
                    firstClient.readLists(true, lists);
                    File positive = new File(exportPath+"n4test0101.json");
                    File negative = new File(exportPath+"n4test0102.json");
                    if (!positive.exists() || negative.exists()) fail ("Ошибка теста № 4 при обработке четного списка");
                }catch (Exception exc){
                    fail("Ошибка теста № 4 при обработке четного списка // "+exc); 
                }
            //нечетное количество > 1
                try{
                    CopyOnWriteArrayList<CopyOnWriteArrayList> lists = filledList(4,2,3);
                    firstClient.readLists(true, lists);
                    File positive = new File(exportPath+"n4test0201.json");
                    File negative1 = new File(exportPath+"n4test0202.json");
                    File negative2 = new File(exportPath+"n4test0203.json");
                    if (!positive.exists() || negative1.exists() || negative2.exists()) fail ("Ошибка теста № 4 при обработке нечетного списка");
                }catch (Exception exc){
                    fail("Ошибка теста № 4 при обработке нечетного списка // "+exc); 
                }
            //список с единственным значением
                try{
                    CopyOnWriteArrayList<CopyOnWriteArrayList> lists = filledList(4,3,1);
                    firstClient.readLists(true, lists);
                    File positive = new File(exportPath+"n4test0301.json");
                    if (!positive.exists()) fail ("Ошибка теста № 4 при обработке списка с единственным значением");
                }catch (Exception exc){
                    fail("Ошибка теста № 4 при обработке списка с единственным значением // "+exc); 
                }
                System.out.println("Тест № 4 завершен: список до сериализации обрабатывается корректно");
        }catch (Exception e){
        fail("Ошибка теста № 4 при запуске // "+e);
        }
    }
    
    @Test //Тест № 5. Клиент - проверка сериализации и десериализации.
    public void testEClientSerialize(){
        System.out.println("Тест № 5");
        
            CrcModel firstClient = spy(new CrcModel());
            firstClient.setPort(port, ipAddress);
            CrcController controller = mock(CrcController.class);
            firstClient.controller = controller;
            firstClient.exportPath = exportPath;
            File export = new File(exportPath);
            if(!export.exists()) export.mkdir();
            firstClient.serialPath = serialPath;
            File serDir = new File(serialPath);
            if(!serDir.exists()) serDir.mkdir();
            CopyOnWriteArrayList<CopyOnWriteArrayList> lists = filledList(5,1,2);
            System.out.println("Тест № 5: сериализация четного списка");
            try{
            firstClient.serialize(lists);
            File serial = new File(serialPath+"list.serial");
            if (!serial.exists()) fail("Ошибка теста № 5 при сериализации четного списка"); 

                try{ //десериализация четного списка
                    System.out.println("Тест № 5: десериализация четного списка");
                    firstClient.deserialize();
                    File positive = new File(exportPath+"n5test0102.json");
                    File negative = new File(exportPath+"n5test0101.json");
                        if (!positive.exists() || negative.exists()) fail ("Ошибка теста № 5 при десериализации четного списка");
                }catch (Exception ex){
                    fail("Ошибка теста № 5 при десериализации четного списка // "+ex);
                }
            
            }catch (Exception e){
                fail("Ошибка теста № 5 при сериализации списка // "+e); 
            } 
            lists = filledList(5,2,3);
            System.out.println("Тест № 5: сериализация нечетного списка");
            try{ //десериализация нечетного списка
                firstClient.serialize(lists);
                File serial = new File(serialPath+"list.serial");
                if (!serial.exists()) fail("Ошибка теста № 5 при сериализации нечетного списка"); 
                try{
                System.out.println("Тест № 5: десериализация нечетного списка");
                firstClient.deserialize();
                    File positive1 = new File(exportPath+"n5test0203.json");
                    File positive2 = new File(exportPath+"n5test0202.json");
                    File negative = new File(exportPath+"n5test0201.json");
                    if (!positive1.exists() || !positive2.exists() || negative.exists()) fail ("Ошибка теста № 5 при десериализации нечетного списка");
                } catch (Exception e){
                    fail("Ошибка теста № 5 при десериализации четного списка // "+e);
                }
            }catch (Exception ex){
                fail("Ошибка теста № 5 при сериализации четного списка // "+ex);
            }
            System.out.println("Тест № 5 завершен: сериализация и десериализация проходят корректно");
    }
    
    @Test //Тест № 6. Сервер - проверка корректности формирования итогового файла.
    public void testFServerCreateFile(){
        System.out.println("Тест № 6");
        CrcChannelHandler handler = mock(CrcChannelHandler.class);
        CrcCalculator calculator = new CrcCalculator("n6test0101", 100000, 10, 0, true, 6, "01.01.2020", serverPath, handler);
        calculator.run();
        File result = new File(serverPath+"n6test0101.json");
        if (!result.exists()){ fail("Ошибка теста № 6 при записи итогового файла");}
        else{
            try{
                ArrayList resList = parseJson(result);
                assertEquals(15, resList.size());
                String sID = (String)resList.get(0);
                double sLoan = (Double)resList.get(1);
                double sRate = (Double)resList.get(2);
                double sFirstPay = (Double)resList.get(3);
                String sAnnuity = (String)resList.get(4);
                long sTerm = (Long)resList.get(5);
                String sPayDate = (String)resList.get(6);
                ArrayList<String> sDates = (ArrayList<String>)resList.get(7);
                ArrayList<Double> sSumPay = (ArrayList<Double>)resList.get(8);
                ArrayList<Double> sPerPay = (ArrayList<Double>)resList.get(9);
                ArrayList<Double> sBasPay = (ArrayList<Double>)resList.get(10);
                ArrayList<Double> sRest = (ArrayList<Double>)resList.get(11);
                System.out.println("Тест № 6 завершен: запись и чтение итогового файла происходят корректно");
            }catch (Exception e){
                fail("Ошибка теста № 6 при чтении итогового файла // "+e);
            }
        }
        
    }
    
    @Test //Тест № 7. Сервер - проверка корректности расчета графика. Аннутитетный платеж без обозначенной суммы первого платежа.
    public void testGServerCalculateAnnZero(){
        System.out.println("Тест № 7");
        CrcChannelHandler handler = mock(CrcChannelHandler.class);
        CrcCalculator calculator = new CrcCalculator("n7test0101", 100000, 10, 0, true, 6, "01.01.2020", serverPath, handler);
        /*Mockito.doNothing().when(calculator).createTable(Matchers.any(String.class), Matchers.any(Double.class), Matchers.any(Double.class), Matchers.any(Double.class), Matchers.any(Boolean.class), 
            Matchers.any(Integer.class), Matchers.any(String.class), Matchers.any(ArrayList.class), Matchers.any(ArrayList.class), Matchers.any(ArrayList.class),
            Matchers.any(ArrayList.class), Matchers.any(ArrayList.class));*/
        calculator.run();
        File result = new File(serverPath+"n7test0101.json");
        try{
                ArrayList resList = parseJson(result);
                ArrayList<Double> sSumPay = (ArrayList<Double>)resList.get(8);
                ArrayList<Double> sPerPay = (ArrayList<Double>)resList.get(9);
                ArrayList<Double> sBasPay = (ArrayList<Double>)resList.get(10);
                ArrayList<Double> sRest = (ArrayList<Double>)resList.get(11);
                double sTotPay = (Double)resList.get(12);
                double sTotPer = (Double)resList.get(13);
                double sTotBas = (Double)resList.get(14);
                //общая сумма платежа
                for(int i=0; i<sSumPay.size(); i++){
                    assertEquals((double)17156.14, (double)sSumPay.get(i), 0.02);
                }
                //процентная часть
                assertEquals((double)833.33, (double)sPerPay.get(0), 0.02);
                assertEquals((double)697.31, (double)sPerPay.get(1), 0.02);
                assertEquals((double)560.15, (double)sPerPay.get(2), 0.02);
                assertEquals((double)421.85, (double)sPerPay.get(3), 0.02);
                assertEquals((double)282.40, (double)sPerPay.get(4), 0.02);
                assertEquals((double)141.79, (double)sPerPay.get(5), 0.02);
                //основной платеж
                assertEquals((double)16322.81, (double)sBasPay.get(0), 0.02);
                assertEquals((double)16458.83, (double)sBasPay.get(1), 0.02);
                assertEquals((double)16595.99, (double)sBasPay.get(2), 0.02);
                assertEquals((double)16734.29, (double)sBasPay.get(3), 0.02);
                assertEquals((double)16873.74, (double)sBasPay.get(4), 0.02);
                assertEquals((double)17014.35, (double)sBasPay.get(5), 0.02);
                //остаток долга
                assertEquals((double)83677.19, (double)sRest.get(0), 0.02);
                assertEquals((double)67218.36, (double)sRest.get(1), 0.02);
                assertEquals((double)50622.38, (double)sRest.get(2), 0.02);
                assertEquals((double)33888.09, (double)sRest.get(3), 0.02);
                assertEquals((double)17014.35, (double)sRest.get(4), 0.02);
                assertEquals((double)0, (double)sRest.get(5), 0.02);
                //итоги
                assertEquals((double)102936.84, sTotPay, 0.01);
                assertEquals((double)2936.84, sTotPer, 0.01);
                assertEquals((double)100000, sTotBas, 0.0);
                System.out.println("Тест № 7 завершен: аннуитетные платежи без заданной суммы первого платежа рассчитываются корректно");
            }catch (Exception e){
                fail("Ошибка теста № 7 при расчете аннуитетных платежей без заданной суммы первого платежа // "+e);
            }
    }
    
    @Test //Тест № 8. Сервер - проверка корректности расчета графика. Аннутитетный платеж с обозначенной суммой первого платежа.
    public void testHServerCalculateAnnNonZero(){
        System.out.println("Тест № 8");
        CrcChannelHandler handler = mock(CrcChannelHandler.class);
        CrcCalculator calculator = new CrcCalculator("n8test0101", 100000, 10, 17156.14, true, 6, "01.01.2020", serverPath, handler);
        calculator.run();
        File result = new File(serverPath+"n8test0101.json");
        try{
                ArrayList resList = parseJson(result);
                ArrayList<Double> sSumPay = (ArrayList<Double>)resList.get(8);
                ArrayList<Double> sPerPay = (ArrayList<Double>)resList.get(9);
                ArrayList<Double> sBasPay = (ArrayList<Double>)resList.get(10);
                ArrayList<Double> sRest = (ArrayList<Double>)resList.get(11);
                double sTotPay = (Double)resList.get(12);
                double sTotPer = (Double)resList.get(13);
                double sTotBas = (Double)resList.get(14);
                //общая сумма платежа
                for(int i=0; i<sSumPay.size(); i++){
                    assertEquals((double)17156.14, (double)sSumPay.get(i), 0.02);
                }
                //процентная часть
                assertEquals((double)0, (double)sPerPay.get(0), 0.02);
                assertEquals((double)863.98, (double)sPerPay.get(1), 0.02);
                assertEquals((double)726.82, (double)sPerPay.get(2), 0.02);
                assertEquals((double)588.52, (double)sPerPay.get(3), 0.02);
                assertEquals((double)449.07, (double)sPerPay.get(4), 0.02);
                assertEquals((double)308.45, (double)sPerPay.get(5), 0.02);
                //основной платеж
                assertEquals((double)17156.14, (double)sBasPay.get(0), 0.02);
                assertEquals((double)16292.16, (double)sBasPay.get(1), 0.02);
                assertEquals((double)16429.32, (double)sBasPay.get(2), 0.02);
                assertEquals((double)16567.62, (double)sBasPay.get(3), 0.02);
                assertEquals((double)16707.07, (double)sBasPay.get(4), 0.02);
                assertEquals((double)16847.69, (double)sBasPay.get(5), 0.02);
                //остаток долга
                assertEquals((double)82843.86, (double)sRest.get(0), 0.02);
                assertEquals((double)66551.7, (double)sRest.get(1), 0.02);
                assertEquals((double)50122.38, (double)sRest.get(2), 0.02);
                assertEquals((double)33554.76, (double)sRest.get(3), 0.02);
                assertEquals((double)16847.69, (double)sRest.get(4), 0.02);
                assertEquals((double)0, (double)sRest.get(5), 0.02);
                //итоги
                assertEquals((double)102936.84, sTotPay, 0.01);
                assertEquals((double)2936.84, sTotPer, 0.01);
                assertEquals((double)100000, sTotBas, 0.0);
                System.out.println("Тест № 8 завершен: аннуитетные платежи с заданной суммой первого платежа рассчитываются корректно");
            }catch (Exception e){
                fail("Ошибка теста № 8 при расчете аннуитетных платежей с заданной суммой первого платежа // "+e);
            }
    }
    
    @Test //Тест № 9. Сервер - проверка корректности расчета графика. Дифференцированный платеж без обозначенной суммы первого платежа.
    public void testIServerCalculateDiffZero(){
        System.out.println("Тест № 9");
        CrcChannelHandler handler = mock(CrcChannelHandler.class);
        CrcCalculator calculator = new CrcCalculator("n9test0101", 100000, 10, 0, false, 6, "01.01.2020", serverPath, handler);
        calculator.run();
        File result = new File(serverPath+"n9test0101.json");
        try{
                ArrayList resList = parseJson(result);
                ArrayList<Double> sSumPay = (ArrayList<Double>)resList.get(8);
                ArrayList<Double> sPerPay = (ArrayList<Double>)resList.get(9);
                ArrayList<Double> sBasPay = (ArrayList<Double>)resList.get(10);
                ArrayList<Double> sRest = (ArrayList<Double>)resList.get(11);
                double sTotPay = (Double)resList.get(12);
                double sTotPer = (Double)resList.get(13);
                double sTotBas = (Double)resList.get(14);
                //общая сумма платежа
                assertEquals((double)17500, (double)sSumPay.get(0), 0.02);
                assertEquals((double)17361.11, (double)sSumPay.get(1), 0.02);
                assertEquals((double)17222.22, (double)sSumPay.get(2), 0.02);
                assertEquals((double)17083.33, (double)sSumPay.get(3), 0.02);
                assertEquals((double)16944.44, (double)sSumPay.get(4), 0.02);
                assertEquals((double)16805.56, (double)sSumPay.get(5), 0.02);
                //процентная часть
                assertEquals((double)833.33, (double)sPerPay.get(0), 0.02);
                assertEquals((double)694.44, (double)sPerPay.get(1), 0.02);
                assertEquals((double)555.56, (double)sPerPay.get(2), 0.02);
                assertEquals((double)416.67, (double)sPerPay.get(3), 0.02);
                assertEquals((double)277.78, (double)sPerPay.get(4), 0.02);
                assertEquals((double)138.89, (double)sPerPay.get(5), 0.05);
                //основной платеж
                for(int i=0; i<sBasPay.size(); i++){
                    assertEquals((double)16666.67, (double)sBasPay.get(i), 0.02);
                }
                //остаток долга
                assertEquals((double)83333.33, (double)sRest.get(0), 0.02);
                assertEquals((double)66666.67, (double)sRest.get(1), 0.02);
                assertEquals((double)50000, (double)sRest.get(2), 0.02);
                assertEquals((double)33333.33, (double)sRest.get(3), 0.02);
                assertEquals((double)16666.67, (double)sRest.get(4), 0.02);
                assertEquals((double)0, (double)sRest.get(5), 0.02);
                //итоги
                assertEquals((double)102916.67, sTotPay, 0.01);
                assertEquals((double)2916.67, sTotPer, 0.01);
                assertEquals((double)100000, sTotBas, 0.0);
                System.out.println("Тест № 9 завершен: дифференцированные платежи без заданной суммы первого платежа рассчитываются корректно");
            }catch (Exception e){
                fail("Ошибка теста № 9 при расчете дифференцированных платежей без заданной суммы первого платежа // "+e);
            }
    }
    
    @Test //Тест № 10. Сервер - проверка корректности расчета графика. Дифференцированный платеж с обозначенной суммой первого платежа.
    public void testJServerCalculateDiffNonZero(){
        System.out.println("Тест № 10");
        CrcChannelHandler handler = mock(CrcChannelHandler.class);
        CrcCalculator calculator = new CrcCalculator("n10test0101", 100000, 10, 17500, false, 6, "01.01.2020", serverPath, handler);
        calculator.run();
        File result = new File(serverPath+"n10test0101.json");
        try{
                ArrayList resList = parseJson(result);
                ArrayList<Double> sSumPay = (ArrayList<Double>)resList.get(8);
                ArrayList<Double> sPerPay = (ArrayList<Double>)resList.get(9);
                ArrayList<Double> sBasPay = (ArrayList<Double>)resList.get(10);
                ArrayList<Double> sRest = (ArrayList<Double>)resList.get(11);
                double sTotPay = (Double)resList.get(12);
                double sTotPer = (Double)resList.get(13);
                double sTotBas = (Double)resList.get(14);
                //общая сумма платежа
                assertEquals((double)17500, (double)sSumPay.get(0), 0.02);
                assertEquals((double)17361.11, (double)sSumPay.get(1), 0.02);
                assertEquals((double)17222.22, (double)sSumPay.get(2), 0.02);
                assertEquals((double)17083.33, (double)sSumPay.get(3), 0.02);
                assertEquals((double)16944.44, (double)sSumPay.get(4), 0.02);
                assertEquals((double)16805.56, (double)sSumPay.get(5), 0.02);
                //процентная часть
                assertEquals((double)0, (double)sPerPay.get(0), 0.02);
                assertEquals((double)861.11, (double)sPerPay.get(1), 0.02);
                assertEquals((double)722.22, (double)sPerPay.get(2), 0.02);
                assertEquals((double)583.33, (double)sPerPay.get(3), 0.02);
                assertEquals((double)444.44, (double)sPerPay.get(4), 0.02);
                assertEquals((double)305.57, (double)sPerPay.get(5), 0.05);
                //основной платеж
                assertEquals((double)17500.00, (double)sBasPay.get(0), 0.02);
                for(int i=1; i<sBasPay.size(); i++){
                    assertEquals((double)16500.00, (double)sBasPay.get(i), 0.02);
                }
                //остаток долга
                assertEquals((double)82500, (double)sRest.get(0), 0.02);
                assertEquals((double)66000, (double)sRest.get(1), 0.02);
                assertEquals((double)49500, (double)sRest.get(2), 0.02);
                assertEquals((double)33000, (double)sRest.get(3), 0.02);
                assertEquals((double)16500, (double)sRest.get(4), 0.02);
                assertEquals((double)0, (double)sRest.get(5), 0.02);
                //итоги
                assertEquals((double)102916.67, sTotPay, 0.01);
                assertEquals((double)2916.67, sTotPer, 0.01);
                assertEquals((double)100000, sTotBas, 0.0);
                System.out.println("Тест № 10 завершен: дифференцированные платежи с заданной суммой первого платежа рассчитываются корректно");
            }catch (Exception e){
                fail("Ошибка теста № 10 при расчете дифференцированных платежей с заданной суммой первого платежа // "+e);
            }
    }
    
    @Test //Тест № 11. Сервер - проверка переноса выходных дней в графике.
    public void testKServerDates(){
        System.out.println("Тест № 11");
        CrcChannelHandler handler = mock(CrcChannelHandler.class);
        CrcCalculator calculator = new CrcCalculator("n11test0101", 100000, 10, 0, true, 6, "29.02.2019", serverPath, handler);
        calculator.run();
        File result = new File(serverPath+"n11test0101.json");
        try{
                ArrayList resList = parseJson(result);
                ArrayList<String> sDates = (ArrayList<String>)resList.get(7);
                assertEquals("01.03.2019", sDates.get(0));
                assertEquals("29.03.2019", sDates.get(1));
                assertEquals("29.04.2019", sDates.get(2));
                assertEquals("29.05.2019", sDates.get(3));
                assertEquals("01.07.2019", sDates.get(4));
                assertEquals("29.07.2019", sDates.get(5));
        }catch (Exception e){
            fail("Ошибка теста № 11 при переносе выходных дней // "+e);
        }
        calculator = new CrcCalculator("n11test0102", 100000, 10, 0, true, 16, "01.12.2019", serverPath, handler);
        calculator.run();
        result = new File(serverPath+"n11test0102.json");
        try{
                ArrayList resList = parseJson(result);
                ArrayList<String> sDates = (ArrayList<String>)resList.get(7);
                assertEquals("02.12.2019", sDates.get(0));
                assertEquals("01.01.2020", sDates.get(1));
                assertEquals("03.02.2020", sDates.get(2));
                assertEquals("02.03.2020", sDates.get(3));
                assertEquals("01.04.2020", sDates.get(4));
                assertEquals("01.05.2020", sDates.get(5));
                assertEquals("01.01.2021", sDates.get(13));
                System.out.println("Тест № 11 завершен: перенос дат производится корректно");
        }catch (Exception e){
            fail("Ошибка теста № 11 при переносе выходных дней // "+e);
        }
    }
    
    
    @Test(timeout=6000) //Тест № 12. Сервер - проверка реакции на передачу клиентом некорректных данных для расчета (устойчивость к ошибке).
    public void testLServerWrongData(){
        System.out.println("Тест № 12");
        try{
        SocketChannel socket = SocketChannel.open(new InetSocketAddress(ipAddress, port));
        DataInputStream clientIn = new DataInputStream(socket.socket().getInputStream());
        DataOutputStream clientOut = new DataOutputStream(socket.socket().getOutputStream());
        String request = "GET /calculate?empty&empty&empty HTTP/1.1\r\n\r\n";
        byte[] byteHeader = request.getBytes();
                clientOut.write(byteHeader,0,request.length());
                clientOut.flush();
                    int stage = 0;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientIn));
                    String header = "";
                    while (stage == 0){
                        header = reader.readLine();
                        stage = 1;
                    }
                    if (header.contains("501")){
                        System.out.println("Тест № 12 завершен: сервер возвращает ошибку 501 при получении неверных данных");
                    }else{
                        fail("Ошибка теста № 12 при обработке сервером неверного/неполного списка");
                    }
        }catch (Exception e){
            fail("Ошибка теста № 12 при обработке сервером неверного/неполного списка // "+e);
        }
    }
    
    @Test(timeout=6000) //Тест № 13. Сервер - проверка реакции на передачу неизвестного запроса.
    //не должно быть зависания
    public void testMServerUnknownRequest(){
        System.out.println("Тест № 13");
        try{
        SocketChannel socket = SocketChannel.open(new InetSocketAddress(ipAddress, port));
        DataOutputStream clientOut = new DataOutputStream(socket.socket().getOutputStream());
        String request = "GET /something HTTP/1.1\r\n\r\n";
        byte[] byteHeader = request.getBytes();
                clientOut.write(byteHeader,0,request.length());
                clientOut.flush();
        }catch (Exception e){
            fail("Ошибка теста № 13 при обработке сервером не предусмотренного кодом запроса // "+e);
        }
    }
    
    @Test(timeout=6000)//Тест № 14. Сервер - одновременный запрос 20 клиентов к серверу.
    public void testNServerManyClients(){
        System.out.println("Тест № 14");
        ExecutorService executor = Executors.newFixedThreadPool(20);
        int c = 1;
        while (c < 21) {
            executor.execute(new callClient(14,c,1));
            c++;
        }
        executor.shutdown();
    }
    
    @Test(timeout=6000)//Тест № 15. Сервер - одновременное подключение 6 клиентов с 6 запросами к серверу.
    public void testOServerManyClientsRequests(){
        System.out.println("Тест № 15");
        ExecutorService executor = Executors.newFixedThreadPool(6);
        int c = 1;
        while (c < 7) {
            executor.execute(new callClient(15,c,6));
            c++;
        }
        executor.shutdown();
    }
    
    @Test //Тест № 16. Сервер - проверка корректности расчетов при заданной сумме первого платежа (аннуитетный платеж) со случайными параметрами:
    //проверяется совпадение итоговых сумм платежей (общих, процентных и по основному долгу) с результатом классического расчета.
    public void testPServerRandomCheck(){
        System.out.println("Тест № 16");
        double loan = 100000 + (10000000 - 100000)* new Random().nextDouble();
        double rate = 5 + (28 - 5)* new Random().nextDouble();
        int term = new Random().nextInt((120 - 6)+1)+6;
        requestRandom("test1601", "annu", 0, 16, loan, rate, term);
            try{
                ArrayList firstList = parseJson(new File(exportPath+"test1601.json"));
                ArrayList<Double> sSumPay = (ArrayList<Double>)firstList.get(8);
                requestRandom("test1602", "annu", sSumPay.get(0), 16, loan, rate, term);
                ArrayList secondList = parseJson(new File(exportPath+"test1602.json"));
                ArrayList<Double> sPerPay = (ArrayList<Double>)secondList.get(9);
                assertFalse((double)sPerPay.get(0)<0);
                assertEquals((double)firstList.get(12), (double)secondList.get(12), 0.001);
                assertEquals((double)firstList.get(13), (double)secondList.get(13), 0.001);
                assertEquals((double)firstList.get(14), (double)secondList.get(14), 0.001);
            }catch (Exception e){
                fail("Ошибка теста № 16 при проверке случайных параметров (аннуитетный платеж) // "+e);
            }
    }
    
    @Test //Тест № 17. Сервер - проверка корректности расчетов при заданной сумме первого платежа (дифференцированный платеж) со случайными параметрами:
    //проверяется совпадение итоговых сумм платежей (общих, процентных и по основному долгу) с результатом классического расчета.
    public void testQServerRandomCheck(){
        System.out.println("Тест № 17");
        double loan = 100000 + (10000000 - 100000)* new Random().nextDouble();
        double rate = 5 + (28 - 5)* new Random().nextDouble();
        int term = new Random().nextInt((120 - 6)+1)+6;
        requestRandom("test1701", "diff", 0, 17, loan, rate, term);
            try{
                ArrayList firstList = parseJson(new File(exportPath+"test1701.json"));
                ArrayList<Double> sSumPay = (ArrayList<Double>)firstList.get(8);
                requestRandom("test1702", "diff", sSumPay.get(0), 17, loan, rate, term);
                ArrayList secondList = parseJson(new File(exportPath+"test1702.json"));
                ArrayList<Double> sPerPay = (ArrayList<Double>)secondList.get(9);
                assertFalse((double)sPerPay.get(0)<0);
                assertEquals((double)firstList.get(12), (double)secondList.get(12), 0.001);
                assertEquals((double)firstList.get(13), (double)secondList.get(13), 0.001);
                assertEquals((double)firstList.get(14), (double)secondList.get(14), 0.001);
            }catch (Exception e){
                fail("Ошибка теста № 17 при проверке случайных параметров (дифференцированный платеж) // "+e);
            }
    }
    
    @Test(timeout=35000) //Тест № 18. Клиент - проверка времени чтения файла с количеством строк около 100 тыс.
    //таймаут должен быть уменьшен при раскрытии секрета быстрого парсинга
    public void testRClientRead100k(){
        try{
            Thread.sleep(2000);
            System.out.println("Тест № 18");
            File txt = new File(importPath+"test100k.csv");
            txt.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter (txt, true));
            for (int i=1; i<100001; i++){
            writer.append ("test18n100calc"+i+";1000000.00;16.50;0.00;annu;36;12.12.2018");
            writer.append(System.getProperty("line.separator"));
            }
            writer.flush();
            writer.close();
                        
            final long start = System.currentTimeMillis();
            CrcModel firstClient = spy(new CrcModel());
            Mockito.doNothing().when(firstClient).readLists(Matchers.any(Boolean.class),Matchers.any(CopyOnWriteArrayList.class));
            firstClient.importer(new File(importPath));
            CopyOnWriteArrayList result = firstClient.lists.get(0);
            assertTrue(result.size()>99999);
            long finish = System.currentTimeMillis();
            System.out.println("Тест № 18 завершен. Время чтения 100 тыс. записей - "+(int)(finish-start)/1000+" сек., размер списка - "+result.size()+" строк");
            txt.delete();
        }catch (Exception e){
            fail("Ошибка теста № 18 при запуске // "+e);
        }
    }
    
    @Test (timeout=20000) //Тест № 19. Сервер - проверка времени подготовки графиков для 100 тыс. запросов (определение производительности алгоритма расчетов).
    public void testSServerCalculatorTimer(){
        System.out.println("Тест № 19");
        try{
            CrcChannelHandler handler = mock(CrcChannelHandler.class);
            final long start = System.currentTimeMillis();
                for (int i=0; i<100000; i++){
                    CrcCalculator calculator = spy(new CrcCalculator("test19n"+i, 100000, 10, 0, true, 24, "01.01.2020", serverPath, handler));
                    Mockito.doNothing().when(calculator).createTable(Matchers.any(String.class), Matchers.any(Double.class), Matchers.any(Double.class), Matchers.any(Double.class), 
                    Matchers.any(Boolean.class), Matchers.any(Integer.class), Matchers.any(String.class), Matchers.any(ArrayList.class), Matchers.any(ArrayList.class), 
                    Matchers.any(ArrayList.class), Matchers.any(ArrayList.class), Matchers.any(ArrayList.class));
                    calculator.run();
                }
            long finish = System.currentTimeMillis();
            System.out.println("Тест № 19 завершен. Время расчета 100 тыс. графиков - "+(int)(finish-start)/1000+" сек.");
        }catch (Exception e){
            fail("Ошибка теста № 19 при запуске // "+e);
        }
    }
    
    @Test (timeout=15000) //Тест № 20.Сервер - проверка времени сохранения 10000 итоговых файлов
    public void testTServerCreateFilesTimer(){
        System.out.println("Тест № 20");
        try{
            CrcChannelHandler handler = mock(CrcChannelHandler.class);
            final long start = System.currentTimeMillis();
            ArrayList<Date> dates = new ArrayList<Date>();
            dates.add(new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2020"));
            dates.add(new SimpleDateFormat("dd.MM.yyyy").parse("01.02.2020"));
            ArrayList<Double> pays = new ArrayList<Double>();
            ArrayList<Double> lbase = new ArrayList<Double>();
            pays.add((double)50000);
            pays.add((double)50000);
            lbase.add((double)50000);
            lbase.add((double)50000);
            ArrayList<Double> lper = new ArrayList<Double>();
            lper.add((double)0);
            lper.add((double)0);
            ArrayList<Double> lrest = new ArrayList<Double>();
            lrest.add((double)50000);
            lrest.add((double)0);
            
                for (int i=0; i<10000; i++){
                    String id = "test20n"+i;
                    CrcCalculator calculator = spy(new CrcCalculator(id, 100000, 0, 0, true, 2, "01.01.2020", serverPath, handler));
                    calculator.createTable(id, 100000, 10, 0, true, 3, "01.01.2020", dates, pays, lper, lbase, lrest);
                }
            long finish = System.currentTimeMillis();
            System.out.println("Тест № 20 завершен. Время сохранения 10000 файлов - "+(int)(finish-start)/1000+" сек.");
        }catch (Exception e){
            fail("Ошибка теста № 20 при сохранении файлов // "+e);
        }
    }
        
    public void createThreeTestFiles(){
        File importDir = new File(importPath);

        if (importDir.exists()){
            File[] oldFiles = importDir.listFiles();
            if (oldFiles != null){
                for(File f : oldFiles){
                        f.delete();
                }
            }
        }else{
            importDir.mkdir();
        }
        
        File tester = new File(importPath+"test01.csv");
            try {
            tester.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter (tester, true));

            writer.append ("test0101"+";1000000.00;16.50;0.00;annu;36;12.12.2018");
            writer.append(System.getProperty("line.separator"));
            writer.append(System.getProperty("line.separator"));
            writer.append ("test0102"+";2000000.00;15.00;45000.00;diff;42;31.03.2019");
            writer.append(System.getProperty("line.separator"));
            writer.append ("test0103"+";500000.00;17.25;32501.65;annu;15;30.11.2019");
            writer.append(System.getProperty("line.separator"));
            writer.append(System.getProperty("line.separator"));
            writer.flush();
            writer.close();            
            }catch (Exception e) {
                System.out.println("Test exception (tester01): "+e);
            } 
            
        tester = new File(importPath+"test02.csv");
            try {
            tester.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter (tester, true));

            writer.append ("test0201"+";1000000.00;16.50;0.00;annu;36;12.12.2018");
            writer.append(System.getProperty("line.separator"));
            writer.append ("test0202"+";100000.00;10.00;0.00;diff;6;15.10.2019");
            writer.append(System.getProperty("line.separator"));
            writer.append ("test0203"+";100000.00;10.00;0.00;annu;6;01.11.2019");
            writer.flush();
            writer.close();            
            }catch (Exception e) {
                System.out.println("Test exception (tester02): "+e);
            }
            
        tester = new File(importPath+"test03.csv");        
            try {
            tester.createNewFile();           
            }catch (Exception e) {
                System.out.println("Test exception (tester03): "+e);
            }
    }
    
    public void startServer(){
        CrcServer server = spy(new CrcServer(port));
        Thread thread = new Thread(server);
        thread.start();
    }
    
    public CopyOnWriteArrayList filledList(int test, int stage, int size){
            CopyOnWriteArrayList<String> listID = new CopyOnWriteArrayList<String>();
            CopyOnWriteArrayList<String> listPaydate = new CopyOnWriteArrayList<String>();
            CopyOnWriteArrayList<String> listLoan = new CopyOnWriteArrayList<String>();
            CopyOnWriteArrayList<String> listPercent = new CopyOnWriteArrayList<String>();
            CopyOnWriteArrayList<String> listPayment = new CopyOnWriteArrayList<String>();
            CopyOnWriteArrayList<String> listTerm = new CopyOnWriteArrayList<String>();
            CopyOnWriteArrayList<String> listAnnu = new CopyOnWriteArrayList<String>();
            CopyOnWriteArrayList<CopyOnWriteArrayList> lists = new CopyOnWriteArrayList<CopyOnWriteArrayList>();
            lists.add(listID);
            lists.add(listLoan);
            lists.add(listPercent);
            lists.add(listPayment);
            lists.add(listAnnu);
            lists.add(listTerm);
            lists.add(listPaydate);
            for (int i=1; i<=size; i++){
                String id = "n"+test+"test0"+stage+"0"+i;
                //System.out.println("GENERATED: "+id);
                listID.add(id);
                listLoan.add("100000.00");
                listPercent.add("10.00");
                listPayment.add("0.00");
                String annu = "annu";
                if(i%2==0) annu="diff";
                listAnnu.add(annu);
                listTerm.add("6");
                listPaydate.add("01.01.2020");
            }
            
            return lists;
    }
    
    public ArrayList parseJson(File file){

        ArrayList<Object> resultList = new ArrayList<Object>();
        try{
        Object object = new JSONParser().parse(new FileReader(file));
        JSONObject json = (JSONObject)object;
        String sID = (String)json.get("id");
        double sLoan = (Double)json.get("loan");
        double sRate = (Double)json.get("rate");
        double sFirstPay = (Double)json.get("firstpay");
        String sAnnuity = (String)json.get("annuity");
        long sTerm = (Long)json.get("term");
        String sPayDate = (String)json.get("firstpaydate");
        //System.out.println("NEW CALCULATOR: "+sID+" / Loan: "+sLoan+" / Rate: "+sRate+" / FirstPay: "+sFirstPay+" / Annuity: "+sAnnuity+" / Term: "+sTerm+" / First date: "+sPayDate);
        ArrayList<String> sDates = new ArrayList<String>();
        ArrayList<Double> sSumPay = new ArrayList<Double>();
        ArrayList<Double> sPerPay = new ArrayList<Double>();
        ArrayList<Double> sBasPay = new ArrayList<Double>();
        ArrayList<Double> sRest = new ArrayList<Double>();
        JSONArray shedule = (JSONArray) json.get("shedule");
        Iterator itrtr = shedule.iterator();
            while(itrtr.hasNext()){
                JSONObject shdlObj = (JSONObject)itrtr.next();
                String tDate = (String)shdlObj.get("date");
                sDates.add(tDate);
                double tSum = (Double)shdlObj.get("summ_pay");
                sSumPay.add(tSum);
                double tPer = (Double)shdlObj.get("percent_pay");
                sPerPay.add(tPer);
                double tBas = (Double)shdlObj.get("basic_pay");
                sBasPay.add(tBas);
                double tRest = (Double)shdlObj.get("rest");
                sRest.add(tRest);
                //System.out.println(tDate+" / "+tSum+" / "+tPer+" / "+tBas+" / "+tRest);
            }
            
        JSONObject totals = (JSONObject)json.get("totals");
        double sTotPay = (Double)totals.get("total_payments");
        double sTotPer = (Double)totals.get("total_percents");
        double sTotBas = (Double)totals.get("total_basics");
        
            resultList.add(sID);
            resultList.add(sLoan);
            resultList.add(sRate);
            resultList.add(sFirstPay);
            resultList.add(sAnnuity);
            resultList.add(sTerm);
            resultList.add(sPayDate);
            resultList.add(sDates);
            resultList.add(sSumPay);
            resultList.add(sPerPay);
            resultList.add(sBasPay);
            resultList.add(sRest);
            resultList.add(sTotPay);
            resultList.add(sTotPer);
            resultList.add(sTotBas);
        //System.out.println("TOTALS// payments: "+sTotPay+" / percents: "+sTotPer+" / base: "+sTotBas);
        }catch (Exception e){}
        return resultList;
    }
    
    public void requestRandom(String name, String annu, double pay, int test, double loan, double rate, int term){
        try{
               SocketChannel socket = SocketChannel.open(new InetSocketAddress(ipAddress, port));
               DataInputStream clientIn = new DataInputStream(socket.socket().getInputStream());
               DataOutputStream clientOut = new DataOutputStream(socket.socket().getOutputStream());

                String parameters = name+"&"+loan+"&"+rate+"&"+pay+"&"+annu+"&"+term+"&01.02.2020";
                String request = "GET /calculate?"+parameters+" HTTP/1.1\r\n\r\n";
                byte[] byteHeader = request.getBytes();
                clientOut.write(byteHeader,0,request.length());
                clientOut.flush();
                int stage = 0;
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientIn));
                String header = "";
                    while (stage == 0){
                        header = reader.readLine();
                        stage = 1;
                        clientOut.writeInt(stage);
                        clientOut.flush();
                    }
                    if(header.contains("200")){
                        int answer = 0;
                        while(stage == 1){
                            answer = clientIn.readInt();
                            stage=2;
                            clientOut.writeInt(stage);
                            clientOut.flush();
                        }
                        
                            try {
                                FileOutputStream out = new FileOutputStream(exportPath+name+".json");
                                byte[] arr = new byte[answer];
                                int count;
                                    while (answer>0 && (count = clientIn.read(arr)) > 0) {
                                        out.write(arr, 0, count);
                                        answer -= count;
                                    }
                                out.close();
                            }catch (Exception e){}   
                    }
            
                
                
           }catch (Exception e){
               fail("Ошибка теста № "+test+" при проверке случайных параметров (аннуитетный платеж) // "+e);
           }
    }
    
    class callClient implements Runnable{
        int test, client, loop;
        public callClient(int test, int client, int loop){
            this.test = test;
            this.client = client;
            this.loop = loop;
        } 

        @Override
        public void run() {
            try{
               SocketChannel socket = SocketChannel.open(new InetSocketAddress(ipAddress, port));
               DataInputStream clientIn = new DataInputStream(socket.socket().getInputStream());
               DataOutputStream clientOut = new DataOutputStream(socket.socket().getOutputStream());
               
               for (int i=0; i<loop; i++){
                double loan = 100000 + (10000000 - 100000)* new Random().nextDouble();
                double rate = 5 + (28 - 5)* new Random().nextDouble();
                String[] types = {"annu","diff"};
                String type = types[new Random().nextInt(types.length)];
                double firstpay = 100000 * new Random().nextDouble();
                int term = new Random().nextInt((120 - 6)+1)+6;
                String parameters = "test"+test+"client"+client+"&"+loan+"&"+rate+"&"+firstpay+"&"+type+"&"+term+"&01.02.2020";
                String request = "GET /calculate?"+parameters+" HTTP/1.1\r\n\r\n";
                byte[] byteHeader = request.getBytes();
                clientOut.write(byteHeader,0,request.length());
                clientOut.flush();
                int stage = 0;
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientIn));
                String header = "";
                    while (stage == 0){
                        header = reader.readLine();
                        stage = 1;
                        clientOut.writeInt(stage);
                        clientOut.flush();
                    }
                    if(header.contains("200")){
                        int answer = 0;
                        while(stage == 1){
                            answer = clientIn.readInt();
                            stage=2;
                            clientOut.writeInt(stage);
                            clientOut.flush();
                        }
                        String fpath = exportPath+"test"+test+"client"+client+".json";
                            try {
                                FileOutputStream out = new FileOutputStream(fpath);
                                byte[] arr = new byte[answer];
                                int count;
                                    while (answer>0 && (count = clientIn.read(arr)) > 0) {
                                        out.write(arr, 0, count);
                                        answer -= count;
                                    }
                                out.close();
                            }catch (Exception e){}
                        File result = new File(fpath);
                        if(result.exists()){
                            System.out.println("Тест № "+test+" для клиента "+client+"(попытка "+(i+1)+") завершен");
                        }else{
                            fail("Ошибка теста № "+test+", клиент № "+client);
                        }
                    }
            }
           }catch (Exception e){
               fail("Ошибка теста № "+test+", клиент № "+client+" // "+e);
           }
        }
    }
}
