View > Tool Windows > App Inspection

```sql
INSERT INTO transactions VALUES
    (NULL, "R$ 100,00", "18/03/2022", "DEBIT", "OUT", 0, "Compra no débito aprovada", "Sua compra no cartão final 0000 no valor de R$ 100,00 em 18/03/2022 às 12:18 em NOME ESTABELECIMENTO SAO PAULO BRA foi aprovada.", "com.c6bank.app", "C6"),
    (NULL, "R$ 1360,68", "18/03/2022", "CREDIT", "OUT", 0, "Compra no crédito aprovada", "Sua compra no cartão final 0000 no valor de R$ 1360,68 em 12/10/2021 às 22:18 em NOME ESTABELECIMENTO SAO PAULO BRA foi aprovada.", "com.c6bank.app", "C6"),
    (NULL, "R$ 15,00", "18/03/2022", "PIX", "OUT", 0, "Transação PIX feita com sucesso!", "Sua transação Pix no valor de R$ 15,00 foi concluída com sucesso", "com.c6bank.app", "C6"),
    (NULL, "R$ 1.234,56", "18/03/2022", "PIX", "IN", 0, "Transação PIX recebida!", "Você recebeu um Pix com valor R$ 1.234,56", "com.c6bank.app", "C6")
    ;
```

Truncate
```sql
DELETE FROM transactions;
UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='transactions';
```

Simulate push


Esse fez disparar o NotificationService.handleNotification(), precisei fazer uma gambizinha? sim....

```shell
adb shell cmd notification post \
    -t "Compra\ no\ débito\ aprovada" \
    awesome_tag \
    "Sua\ compra\ no\ cartão\ final\ 0000\ no\ valor\ de\ R$\ 100,00\ em\ 18/03/2022\ às\ 12:18\ em\ NOME\ ESTABELECIMENTO\ SAO\ PAULO\ BRA\ foi\ aprovada." 
```

```shell
adb shell am broadcast -a "com.c6bank.app" \
                      -e "android.title" "Test Notification Title" \
                      -e "android.text" "Test Notification Text"
```


`adb shell am broadcast -a com.google.android.c2dm.intent.RECEIVE`      

`adb shell am broadcast -n br.com.neuberoliveira.finance.service.NotificationService -a com.google.android.c2dm.intent.RECEIVE -e key "data"`

`adb shell am broadcast -n com.c6bank.app -a com.google.android.c2dm.intent.RECEIVE -e key "data"`  