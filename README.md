# Janra.Jowi

Currently at MVP Functionality

Java HTTP self hosting framework. The idea is to configure pipelines to handle HTTP requests in a similar way to the .net OWIN framework.

This basically works but it has limited capability. It has no security yet, or robustness around any interrupted or slow network traffic. Many elements of HTTP still need to be added but it does work - see the examples.

## Working
1. Registered routes with config
1. Passing through of incoming Request including headers
1. Allow middleware to build response
1. Compose response from middleware 'build'
1. Limit concurrent request handling (max threads in config)
1. Implement chunking (ensure last encoding)
1. Have chunking transfer-encoding override content-length

## Current Work

1. Interpret incoming charset in header and use it (rather than using default ISO-8859-1)
1. Build Integeration tests:
Current thinking:
   Use cucumber running tests in one container with framework running in a second. Use feedback middleware for framework to allow
   cucumber to get results
   
 ## Backlog
 
 1. Use Double for content length (and chunk length)
 1. Build remaining Transfer encoding decoders
 1. Extract resource identifer(s) from uri
 
 There will be more, not worked it all out yet
