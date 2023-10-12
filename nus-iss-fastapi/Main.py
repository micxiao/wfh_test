from PIL import Image
import numpy as np
import os
from io import BytesIO
import pickle
import base64



def load_image(bytes_data):
    bytes_data = base64.b64decode(bytes_data)
    image = BytesIO(bytes_data)
    img = Image.open(image)
    if img.mode != 'RGB':
        img = img.convert('RGB')
    img = img.resize((32, 32))
    np_img = np.array(img) / 255.0
    print(np_img.shape)

    np_img = np_img.reshape(-1)

    return np_img


def svm_classifier(np_img):
    with open('svm_classifier.pkl', 'rb') as f:
        loaded_classifier = pickle.load(f)
    result = loaded_classifier.predict([np_img])[0]
    if result == 1:
        return 'Image is REAL'
    else:
        return 'Image is FAKE'

