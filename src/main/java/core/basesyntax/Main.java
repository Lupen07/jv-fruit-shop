package core.basesyntax;

import core.basesyntax.db.FileReadService;
import core.basesyntax.db.FileReadServiceImpl;
import core.basesyntax.db.Storage;
import core.basesyntax.db.WriteDataToFile;
import core.basesyntax.db.WriteDataToFileImpl;
import core.basesyntax.service.accountingreport.Accounting;
import core.basesyntax.service.accountingreport.AccountingImpl;
import core.basesyntax.service.accountingreport.TransactionProcessor;
import core.basesyntax.service.accountingreport.TransactionProcessorImpl;
import core.basesyntax.service.calculator.BalanceCalculatorImpl;
import core.basesyntax.service.calculator.OperationCalculator;
import core.basesyntax.service.calculator.PurchaseCalculatorImpl;
import core.basesyntax.service.calculator.ReturnCalculatorImpl;
import core.basesyntax.service.calculator.SupplyCalculatorImpl;
import core.basesyntax.service.operation.FruitOperation;
import core.basesyntax.service.operation.OperationParser;
import core.basesyntax.service.operation.OperationParserImpl;
import core.basesyntax.service.strategy.OperationStrategyImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static final String INPUT_FILE = "src/main/resources/InputFile.csv";
    private static final String REPORT_FILE = "src/main/resources/Report.csv";

    public static void main(String[] args) {
        Map<FruitOperation.Operation, OperationCalculator> operationStrategyMap = new HashMap<>();
        operationStrategyMap.put(FruitOperation.Operation.BALANCE, new BalanceCalculatorImpl());
        operationStrategyMap.put(FruitOperation.Operation.PURCHASE, new PurchaseCalculatorImpl());
        operationStrategyMap.put(FruitOperation.Operation.RETURN, new ReturnCalculatorImpl());
        operationStrategyMap.put(FruitOperation.Operation.SUPPLY, new SupplyCalculatorImpl());

        FileReadService readService = new FileReadServiceImpl();
        List<String> dataFromReport = readService.readDataFromReport(INPUT_FILE);

        OperationParser transactionParser = new OperationParserImpl();
        List<FruitOperation> fruitTransaction =
                transactionParser.getFruitTransaction(dataFromReport);

        TransactionProcessor transactionProcessor = new TransactionProcessorImpl(
                new OperationStrategyImpl(operationStrategyMap)
        );
        transactionProcessor.process(fruitTransaction);

        Accounting accounting = new AccountingImpl();
        String account = accounting.accountingReport(Storage.getFruitKindsAndQuantity());

        WriteDataToFile writeDataToFile = new WriteDataToFileImpl();
        writeDataToFile.writeDataToFile(account, REPORT_FILE);
    }
}
