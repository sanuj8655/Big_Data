#!/usr/bin/python3

import tweepy
import json


class MyStreamListener(tweepy.StreamListener):
    def on_error(self, status_code):
        if status_code == 420:
            print("------------ limit exceeds ------------")
            return False

    def on_status(self, status):
        tweet = dict()
        tweet['id'] = status.id_str
        tweet['time'] = status.timestamp_ms
        try:
            tweet_text = status.extended_tweet.full_text
        except:
            tweet_text = status.text
        tweet['text'] = tweet_text
        if len(tweet_text) > 0:
            # write tweet on disk
            file_path = "D:/tmp/tweets_new/" + status.id_str + ".json"
            file = open(file_path, 'w')
            file.write(json.dumps(tweet))
            file.close()
            # print tweet on terminal
            print(tweet)


if __name__ == '__main__':
    consumer_key = "J52ZqSAgwa3gXaeKT9aOF5gHc"
    consumer_secret = "8dBYA7uRbjKDOqg4eRLGT7MXFWq3cYRaiwjPsxrOS3qEbnIPIM"
    access_token = "728995019101626369-d78c8YNoelHRLtwYPhpqccdvwW0jG3C"
    access_token_secret = "FWjxMuccU2H2d4YFviixzFYkznln0mGUywmjhrq9i27ZE"

    auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
    auth.set_access_token(access_token, access_token_secret)

    api = tweepy.API(auth)

    myStreamListener = MyStreamListener()
    myStream = tweepy.Stream(auth=api.auth, listener=myStreamListener)

    myStream.filter(track=[' ', 'the', 'i', 'to', 'a', 'and', 'is', 'in', 'it'], languages=['en'])
