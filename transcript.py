import sys
from youtube_transcript_api import YouTubeTranscriptApi

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("No video ID provided", file=sys.stderr)
        sys.exit(1)

    video_id = sys.argv[1]
    try:
        transcript = YouTubeTranscriptApi.get_transcript(video_id)
        for entry in transcript:
            print(entry['text'])
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)
