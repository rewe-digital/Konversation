# File Formats

Currently are two file formats supported `ksv` and `grammar` files. For defining slot-values or entities you
can use the syntax of `value` files.

## ksv files
The syntax of ksv files are an invention of [René Kilczan](https://github.com/rekire) and [Melanie Weitz].

## grammar files

The syntax of grammar files is invented by [Kay Lerch](https://github.com/KayLerch) for his
[Alexa Skill Utterance and Schema Generator](https://github.com/KayLerch/alexa-utterance-generator).

The restriction of grammar files is that you cannot define your prompts, reprompts and suggestions. Why you
should my still prefer this tool instead of the Utterance Generator, is that each generated utterance is cached.
That makes the generation of the Alexa Schema much faster. You could even generate millions of utterances within
an acceptable time frame. Even if you plan to migrate you can use just your grammar files for the beginning.

The best thing is we have a cli which is a faster and has a lower memory footprint.

Check also the guide for [migrate your grammar files](./grammar-file-migration.md) and full specification of the
[grammar file format](https://github.com/KayLerch/alexa-utterance-generator#31-grammar-syntax-for-sample-utterance-definitions).


### Restrictions
Please note that for right now the intent names have to be in its independed line, that inline syntax is
currently not supported. Also the slot value inline format is current not supported. The workaround is
to copy that blocks to own files. E.g. you have

    {Ingredient}: Egg, Suggar, Floor, Water

You need to store those values in a file called `values/Ingredient.values` with the content:

    Egg
    Suggar
    Floor
    Water

Extending system slots like in this example is not supported too:

    {AMAZON.US_CITY}: new york, big apple

## value files
