package com.example.sbt.features.user.batch;

import com.example.sbt.features.user.entity.User;
import com.example.sbt.infrastructure.web.config.CustomProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ExportUsersBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public UserRowMapper userRowMapper() {
        return new UserRowMapper();
    }

    @Bean
    public JdbcCursorItemReader<User> userReader(UserRowMapper userRowMapper) {
        JdbcCursorItemReader<User> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT * FROM _user");
        reader.setRowMapper(userRowMapper);
        reader.setFetchSize(500);
        return reader;
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<User> userWriter(@Value("#{jobParameters['outputFile']}") String outputFile) {
        BeanWrapperFieldExtractor<User> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[]{"id", "username", "email"});
        
        log.info("hello {}", outputFile);

        DelimitedLineAggregator<User> aggregator = new DelimitedLineAggregator<>();
        aggregator.setDelimiter(",");
        aggregator.setFieldExtractor(extractor);

        return new FlatFileItemWriterBuilder<User>()
                .name("userCsvWriter")
                .resource(new FileSystemResource(".temp/export-users.csv"))
                .headerCallback(writer -> writer.write("id,username,email"))
                .lineAggregator(aggregator)
                .build();
    }

    @Bean
    public Step exportUsersStep(JdbcCursorItemReader<User> userReader, FlatFileItemWriter<User> userWriter) {
        return new StepBuilder("exportUsersStep", jobRepository)
                .<User, User>chunk(500, transactionManager)
                .reader(userReader)
                .writer(userWriter)
                .build();
    }

    @Bean
    public Job exportUsersJob(Step exportUsersStep) {
        return new JobBuilder("exportUsersJob", jobRepository)
                .start(exportUsersStep)
                .build();
    }
}
