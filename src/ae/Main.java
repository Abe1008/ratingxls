/*
 * Copyright (c) 2019-2023. Aleksey Eremin
 * 03.09.2019
 * 21.12.2023
 */
/*
 * Чтение данных рейтинга из web-страницы и формирование Excel файла.
 * Код ошибки приложения:
 *  1 - ошибка чтения web-страницы
 *  2 - вывод хелпа
 *  3 - невозможно записать выходной файл
 *
 */

package ae;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	      // write your code here
        System.out.println("Данные рейтинга. " + R.Ver);
        R.loadDefault();
        // адрес WEB-страницы с данными e-mail агентов
        String webpage = R.XRating;
        String outdir  = R.workDir;
        try {
            for (int i = 0; i < args.length; i++) {
                String key = args[i];

                switch (key) {
                    case "-?":
                        System.out.println(HelpMessage);
                        System.exit(2);
                        return;

                    case "-w":  // web-страница
                        i++;
                        webpage = args[i];  // web-страница
                        break;

                    case "-o":  // выходной каталог
                        i++;
                        outdir = args[i];  // web-страница
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println(ErrMessage);
            System.exit(2);
        }

        // будем читать web-страницу
        R.XRating = webpage;   //  адрес WEB-страницы с данными
        ContentHttp conth = new ContentHttp();
        String txt = conth.getContent(webpage); // загрузим
        if (txt == null) {
            System.out.println("Не могу загрузить страницу: " + webpage);
            System.exit(1);
        }

        // будем считывать csv файл с рейтингом
        // 2023-12-21;26121;Пермский край;ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ "ИНТЕРНЕТ ПЛЮС";59;5903089143;0.898;537578;4826;Ростелеком;2023-12-22 21:54:14
        //
        ArrayList<String[]> arrlst = new ArrayList<>();
        int cnt = 0;
        Scanner scanner = new Scanner(txt);
        while(scanner.hasNextLine()) {
            String str = scanner.nextLine();
            //str = str.replace("'", "\"");  // заменить одинарные кавычки
            String[] rst = str.split(";");
            if(11 == rst.length ) {
                cnt++;
                arrlst.add(rst);
            } else {
                System.err.println("?-Warning-неправильный формат входной строки: " + str);
            }
        }
        System.out.println("Прочитано строк: " + cnt);

        FormaXls f = new FormaXls();
        String outFile = f.makeList(arrlst, outdir);
        System.out.println("output file: " + outFile);
    }

    private final static String HelpMessage =
        "Чтение данных рейтинга из web-страницы и формирование Excel файла. " + R.Ver + "\n" +
        "Help about program:\n" +
        ">ratingxls [-w adres] [-o outdir]\n" +
        " -w adres  адрес web-страницы с данными рейтинга (" + R.XRating + ")\n" +
        " -o outdir выходной каталог (" + R.workDir +")";

    private final static String ErrMessage =
        "Чтение данных рейтинга из web-страницы и формирование Excel файла. " + R.Ver + "\n" +
        "Неправильный формат командной строки. Смотри -?";

}
