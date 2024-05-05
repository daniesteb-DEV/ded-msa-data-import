package dev.daniesteb.ded.data.imports.service.impl;

import dev.daniesteb.ded.data.imports.domain.DataImport;
import dev.daniesteb.ded.data.imports.domain.DetailValidatedFile;
import dev.daniesteb.ded.data.imports.domain.FileDetailError;
import dev.daniesteb.ded.data.imports.service.DataImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataImportServiceImpl implements DataImportService {

    @Override
    public Mono<DetailValidatedFile> importFileData(DataImport dataImport) {
        log.info("|-> importFileData started in service");
        return Mono.just(dataImport)
                   .mapNotNull(dataImportRequest -> getFileFromBase64(dataImport.getFileInfo()
                                                                                .getFileType(),
                                                                      dataImportRequest.getFileInfo()
                                                                                       .getFileBase64()))
                   .mapNotNull(file -> validateFile(file, dataImport.getFileInfo()
                                                                    .getFileType()))
                   .doOnSuccess(response -> log.info("|-> importFileData finished successfully."))
                   .doOnError(error -> log.error("|-> importFileData finished with error. ErrorDetail: {}",
                                                 error.getMessage()));
    }

    private static DetailValidatedFile validateFile(File file, String fileType) {
        DetailValidatedFile detailValidatedFile;
        switch (fileType) {
            case "seating" -> detailValidatedFile = validateFileSeatingData(file);
            case "amortization" -> detailValidatedFile = validateFileAmortizationData(file);
            default -> detailValidatedFile = null;
        }
        return detailValidatedFile;
    }

    private static File getFileFromBase64(String fileType, String fileBase64) {
        try {
            byte[] dataBytes = Base64.decodeBase64(fileBase64);
            File file = File.createTempFile("temp" + fileType, "xlsx");
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
            outputStream.write(dataBytes);
            return file;
        } catch (IOException ex) {
            return null;
        }
    }

    private static DetailValidatedFile validateFileSeatingData(File fileWithData) {
        try {
            DetailValidatedFile detailValidatedFile = new DetailValidatedFile();
            List<FileDetailError> fileDetailErrorList = new ArrayList<>();
            FileInputStream file = new FileInputStream(fileWithData);
            XSSFWorkbook wb = new XSSFWorkbook(file);
            XSSFSheet ws = wb.getSheetAt(0);
            for (Row row : ws) {
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (Objects.requireNonNull(cell.getCellType()) == CellType.BLANK) {
                        fileDetailErrorList.add(FileDetailError.builder()
                                                               .line(cell.getRowIndex() + "" + cell.getColumnIndex())
                                                               .description("Cell is empty value")
                                                               .build());
                    }
                }
            }
            file.close();
            detailValidatedFile.setFileDetailErrors(fileDetailErrorList);
            return detailValidatedFile;
        } catch (Exception ex) {
            return null;
        }
    }

    private static DetailValidatedFile validateFileAmortizationData(File fileWithData) {
        try {
            DetailValidatedFile detailValidatedFile = new DetailValidatedFile();
            List<FileDetailError> fileDetailErrorList = new ArrayList<>();
            FileInputStream file = new FileInputStream(fileWithData);
            XSSFWorkbook wb = new XSSFWorkbook(file);
            XSSFSheet ws = wb.getSheetAt(0);
            for (Row row : ws) {
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (Objects.requireNonNull(cell.getCellType()) == CellType.BLANK) {
                        fileDetailErrorList.add(FileDetailError.builder()
                                                               .line(cell.getRowIndex() + "" + cell.getColumnIndex())
                                                               .description("Cell is empty value")
                                                               .build());
                    } else {
                        fileDetailErrorList.add(getObjectDependCellType(cell));
                    }
                }
            }
            file.close();
            detailValidatedFile.setFileDetailErrors(fileDetailErrorList);
            return detailValidatedFile;
        } catch (Exception ex) {
            return null;
        }
    }

    private static FileDetailError getObjectDependCellType(Cell cell) {
        FileDetailError fileDetailError = new FileDetailError();
        switch (cell.getCellType()) {
            case NUMERIC -> fileDetailError = FileDetailError.builder()
                                                             .line(cell.getRowIndex() + "" + cell.getColumnIndex())
                                                             .description("Cell contains wrong value")
                                                             .value(cell.getNumericCellValue() + "")
                                                             .build();
            case STRING -> fileDetailError = FileDetailError.builder()
                                                            .line(cell.getRowIndex() + "" + cell.getColumnIndex())
                                                            .description("Cell contains wrong value")
                                                            .value(cell.getStringCellValue())
                                                            .build();
        }
        return fileDetailError;
    }
}
