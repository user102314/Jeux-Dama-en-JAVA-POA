module Porjet {
    requires java.sql;
    requires java.desktop;

    // Modules Jackson
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    // Exporter le package 'util' vers Jackson
    exports util to com.fasterxml.jackson.databind;
    exports model to com.fasterxml.jackson.databind;
    opens util to com.fasterxml.jackson.databind;
    opens model to com.fasterxml.jackson.databind;
}