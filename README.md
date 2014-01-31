# FavColor Android client

Android app which is a FavColor client; FavColor is a demo app for a variety of identity technologies, including OAuth 2, OpenID COnnect, and Persona.

This generates two APKs, called “FavColor” and “FC + GitKit”.  The former uses pure OpenID Connect tokens for authentication to talk to the server and only works with Google accounts.  The latter uses the Google Identity Toolkit libraries and allows sign-in via Facebook, Yahoo, or Google, or with an email address and password.

## Setup

I just pushed the Eclipse project directory.  If you’re using IntelliJ or something, sorry.

There are dependencies on three library projects: the AmbilWarna color picker, the Identity Toolkit SDK, and the Identity-Toolkit Facebook SDK shim.

