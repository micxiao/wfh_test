import logging
import ast
from fastapi import FastAPI, HTTPException, Request
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware
import uvicorn
import json
from Main import *

app = FastAPI(title="nus-iss-API",)

app.add_middleware(
    CORSMiddleware,
    allow_origins=['*'],
    allow_credentials=True,
    allow_methods=['*'],
    allow_headers=['*'],
)

@app.post("/")
async def process_request(request: Request):
    logging.info('Processing a request.')
    data = await request.json()
    bytes_data = data['bytes_data']
    np_img = load_image(bytes_data)
    proba_svm, result_svm = svm_classifier(np_img)
    proba_cnn, result_cnn = cnn_classifier(np_img)
    print({'proba_svm': type(proba_svm), 'result_svm': type(result_svm) , 'proba_cnn': type(proba_cnn), 'result_cnn': type(result_cnn)})
    return {'proba_svm': proba_svm, 'result_svm': result_svm , 'proba_cnn': proba_cnn, 'result_cnn': result_cnn}
    # return "done"

if __name__ == "__main__":
   uvicorn.run("nus-iss:app", host="0.0.0.0", port=8000, reload=True)
