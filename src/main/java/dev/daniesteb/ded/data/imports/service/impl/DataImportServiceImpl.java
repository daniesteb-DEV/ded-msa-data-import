package dev.daniesteb.ded.data.imports.service.impl;

import com.azure.core.util.FluxUtil;
import dev.daniesteb.ded.data.imports.domain.DetailValidatedFile;
import dev.daniesteb.ded.data.imports.domain.FileDetailError;
import dev.daniesteb.ded.data.imports.domain.FileInfo;
import dev.daniesteb.ded.data.imports.service.DataImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataImportServiceImpl implements DataImportService {

    @Override
    public Mono<DetailValidatedFile> importFileData(Flux<Part> file, String fileTemplateType) {
        log.info("|-> importFileData started in service");
        return createFileToProcess(file).map(fileToProcess -> validateFile(fileToProcess, fileTemplateType))
                                        .doOnSuccess(response -> log.info(
                                                "|-> importFileData in service finished successfully."))
                                        .doOnError(error -> log.error(
                                                "|-> importFileData in service finished with error. ErrorDetail: {}",
                                                error.getMessage()));
    }

    @Override
    public Mono<FileInfo> uploadFile(Flux<Part> file) {
        log.info("|-> uploadFile started in service");
        return createFileToProcess(file).map(fileToProcess -> FileInfo.builder()
                                                                      .fileId(Objects.requireNonNull(fileToProcess)
                                                                                     .getName()
                                                                                     .split("\\.")[0])
                                                                      .build())
                                        .doOnSuccess(response -> log.info(
                                                "|-> uploadFile {} in service finished successfully.",
                                                response.getFileId()))
                                        .doOnError(error -> log.error(
                                                "|-> uploadFile in service finished with error. ErrorDetail: {}",
                                                error.getMessage()));
    }

    @Override
    public Mono<DetailValidatedFile> validateFileData(FileInfo fileInfo) {
        log.info("|-> validateFileData started in service");
        return Mono.empty();
    }

    private static Mono<File> createFileToProcess(Flux<Part> file) {
        return FluxUtil.collectBytesInByteBufferStream(file.filter(part -> part instanceof FilePart)
                                                           .ofType(FilePart.class)
                                                           .flatMap(filePart -> filePart.content()
                                                                                        .map(dataBuffer -> {
                                                                                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                                                                            dataBuffer.read(
                                                                                                    bytes);
                                                                                            DataBufferUtils.release(
                                                                                                    dataBuffer);
                                                                                            return bytes;
                                                                                        }))
                                                           .map(ByteBuffer::wrap))
                       .mapNotNull(bytes -> createFileTemp(bytes,
                                                           UUID.randomUUID()
                                                               .toString()));
    }

    private static File createFileTemp(byte[] byteData, String fileName) {
        try {
            File fileToValid = File.createTempFile(fileName, ".xlsx");
            Files.write(fileToValid.toPath(), byteData);
            return fileToValid;
        } catch (Exception e) {
            return null;
        }
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
