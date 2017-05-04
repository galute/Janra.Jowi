# Janra.Jowi

Currently at MVP Functionality

Java HTTP self hosting framework. The idea is to configure pipelines to handle HTTP requests in a similar way to the .net OWIN framework.

This basically works but it has limited capability. It has no security yet, or robustness around any interrupted or slow network traffic. Many elements of HTTP still need to be added but it does work - see the examples.

## Working
Registered routes with config
Passing through of incoming Request including headers
Allow middleware to build response
compose response from middleware 'build'
Limit concurrent request handling (max threads in config)
Implement chunking (ensure last encoding)
Have chunking transfer-encoding override content-length

## Current Work

Interpret incoming charset in header and use it (rather than using default ISO-8859-1)

Build Integeration tests
Current thinking:
   Use cucumber running tests in one container with framework running in a second. Use feedback middleware for framework to allow
   cucumber to get results
   
 ## Backlog
 
 Use Double for content length (and chunk length)
 Build remaining Transfer encoding decoders
 Extract resource identifer(s) from uri
 
 There will be more, not worked it all out yet
