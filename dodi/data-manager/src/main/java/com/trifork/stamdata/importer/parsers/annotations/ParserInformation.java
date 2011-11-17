package com.trifork.stamdata.importer.parsers.annotations;

public @interface ParserInformation
{
    /**
     * An ID that is used to uniquely identifies
     * a parser.
     *
     * The id must be in ascii and alphanumeric.
     *
     * @return a non-null, non-empty string.
     */
    String id();

    /**
     * A human readable name for the parser.
     *
     * @return a non-null, non-empty string.
     */
    String name();
}
