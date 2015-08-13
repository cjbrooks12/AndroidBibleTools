# AndroidBibleTools

## What is it?
AndroidBibleTools is a library for Android designed to make sense of verses from the Bible while providing several peripherals and tools to manage and share your verses in a meaningful way. The tools include the following major points:
#####<i>Mimic the Bible</i>
Treat verse objects as if they were the actual verses in your physical Bible. If two verses represent the same location in the Bible, then they are considered equal, regardless of the language or translation used. This allows for verses to be sorted in the order they lie in the Bible, and it makes sharing a verse with a friend simple. And since verses refer to more than a bit of text, cross-references and footnotes all made easily accessible, and in such as way that the end user doesn't feel like they are missing any kind of functionality.

#####<i>Take the headache out of the Bible</i>
Downloading verses is not easy. Parsing text to find references is quite difficult. Getting everyone to share verses in any meaningful way is downright impossible. That is, it would be if not for AndroidBibleTools. Leave all the hard stuff up to me, and just go make wonderful apps, and together, let's turn a pool of Christian apps into a thriving ecosystem. 

#####<i>Share the Bible</i>
God gave us the Bible to be shared. This library is designed to compensate for the current lack of Verse sharing by utilizing a very good parser to extract the references sent in a standard Android Intent. But this is just the beginning, because receiving a shared verse should not be that hard. If we all used a standard format to share verses, then there would be no issues in compatibility between verses, and sending verses between apps would actually feel like sending verses, not just sending a Tweet with a verse.

## Why
There is a big problem with Christian apps on Android: none of them can talk to each other. When you take a picture, any app can receive that picture and do what it wants with it, without every app needing to create its own photo format. So why isn't it just as easy to share Bible verses, which were <i>made to be shared</i>. 

This library solves that problem. By making it dead-simple to share verses to other apps, developers can stop worrying about how to handle incoming Intents, and instead just pass the incoming data to the library and get back exactly what the other app sent, not some useless string with Bible text and a URL. That doesn't help anyone, and is good only for sharing on Twitter. 

...

## Include in your project 
#### Gradle
This library uses Jitpack to distribute the release sources. To use, you must add the Jitpack repository to your top-level build.gradle file.
```groovy
repositories {
    maven {
        url "https://jitpack.io"
    }
}
```
Add the following to your module's build.gradle file:
```groovy
dependencies {
    compile 'com.github.cjbrooks12:AndroidBibleTools:0.1.0@aar'
}
```

And that's it!  You can see it's usage in my other apps [Scripture Now!](https://github.com/cjbrooks12/scripturememory) and [OpenBible.info for Android](https://github.com/cjbrooks12/openbible), or in the project included here abttestapp. The code in Scripture Now will likely be the most recent of the three, but the code in the test app will probably be the most accessible.

[Learn more about Jitpack](https://jitpack.io/#cjbrooks12/AndroidBibleTools/0.1.0)

###Eclipse
You will have to manually download the repository as a zip and include it yourself.


## Developers
Casey Brooks
