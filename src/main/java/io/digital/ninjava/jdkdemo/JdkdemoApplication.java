package io.digital.ninjava.jdkdemo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
public class JdkdemoApplication {

    @RequiredArgsConstructor
    enum Demo {
        NOOP((args) -> {
            log.info("Please provide a valid demo as first argument");
        }),
        NPE((args) -> {
            Long a = 1L, b=2L, c=null;
            log.info("{} * {} * {} = {}", a, b, c, a * b * c);
        });

        private final Consumer<String[]> demo;

        public void go(String... args) {
            demo.accept(args);
        }

        public static Demo of(String val) {
            return Stream.of(values())
                    .filter(candidate -> candidate.name().equals(val))
                    .findFirst()
                    .orElse(NOOP);
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            Demo.of(args[0]).go(Arrays.copyOfRange(args, 1, args.length));
        }
    }

}
